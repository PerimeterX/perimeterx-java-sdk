package com.perimeterx.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.perimeterx.http.async.PxClientAsyncHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.EnforcerTelemetry;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.models.risk.S2SErrorReason;
import com.perimeterx.models.risk.S2SErrorReasonInfo;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.JsonUtils;
import com.perimeterx.utils.PXCommonUtils;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Low level HTTP client
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXHttpClient implements PXClient, Closeable {
    private static final int INACTIVITY_PERIOD_TIME_MS = 1000;
    private static final long MAX_IDLE_TIME_SEC = 30L;

    private static final PXLogger logger = PXLogger.getLogger(PXHttpClient.class);

    private static final Charset UTF_8 = Charset.forName("utf-8");

    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient asyncHttpClient;
    private PoolingNHttpClientConnectionManager nHttpConnectionManager;
    private final TimerValidateRequestsQueue timerConfigUpdater;
    private PXConfiguration pxConfiguration;

    public PXHttpClient(PXConfiguration pxConfiguration) throws PXException {
        this.pxConfiguration = pxConfiguration;
        initHttpClient();
        try {
            initAsyncHttpClient();
        } catch (IOReactorException e) {
            throw new PXException(e);
        }

        this.timerConfigUpdater = new TimerValidateRequestsQueue(nHttpConnectionManager, pxConfiguration);
        timerConfigUpdater.schedule();
    }

    private void initHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(this.pxConfiguration.getMaxConnections());
        cm.setDefaultMaxPerRoute(this.pxConfiguration.getMaxConnectionsPerRoute());
        cm.setValidateAfterInactivity(INACTIVITY_PERIOD_TIME_MS);

        httpClient = HttpClients.custom()
                .evictExpiredConnections()
                .evictIdleConnections(MAX_IDLE_TIME_SEC, TimeUnit.SECONDS)
                .setConnectionManager(cm)
                .setDefaultHeaders(PXCommonUtils.getDefaultHeaders(pxConfiguration.getAuthToken()))
                .build();
    }

    private void initAsyncHttpClient() throws IOReactorException {
        DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();

        ioReactor.setExceptionHandler(new IOReactorExceptionHandler() {
            @Override
            public boolean handle(IOException ex) {
                logger.error("IO Reactor encountered an IOException, shutting down reactor. {}", ex);
                return false;
            }

            @Override
            public boolean handle(RuntimeException ex) {
                logger.error("IO Reactor encountered a RuntimeException, shutting down reactor. {}", ex);
                return false;
            }
        });

        nHttpConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
        CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(nHttpConnectionManager)
                .build();
        closeableHttpAsyncClient.start();
        asyncHttpClient = closeableHttpAsyncClient;
    }

    @Override
    public RiskResponse riskApiCall(PXContext pxContext) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = createRequestBody(pxContext);
            if (requestBody == null) {
                return null;
            }

            httpResponse = executeRiskAPICall(requestBody, pxContext);
            if (httpResponse == null) {
                return null;
            }

            pxContext.setMadeS2SApiCall(true);
            return validateRiskAPIResponse(httpResponse, pxContext);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    private String createRequestBody(PXContext pxContext) {
        try {
            RiskRequest riskRequest = RiskRequest.fromContext(pxContext);
            String requestBody = JsonUtils.writer.writeValueAsString(riskRequest);
            logger.debug("Risk API Request: {}", requestBody);
            return requestBody;
        } catch (JsonProcessingException e) {
            handleException(pxContext, e, S2SErrorReason.UNABLE_TO_SEND_REQUEST, null);
            return null;
        }
    }

    private CloseableHttpResponse executeRiskAPICall(String requestBody, PXContext pxContext) throws ConnectTimeoutException {
        HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_RISK);
        post.setEntity(new StringEntity(requestBody, UTF_8));
        post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));

        try {
            return httpClient.execute(post);

        } catch (ConnectTimeoutException e) {
            logger.debug("ConnectTimeoutException", e);
            throw e;
        } catch (SocketTimeoutException e) {
            logger.debug("SocketTimeoutException", e);
            throw new ConnectTimeoutException(e.getMessage());
        } catch (IOException e) {
            handleException(pxContext, e, S2SErrorReason.UNABLE_TO_SEND_REQUEST, null);
        }
        return null;
    }

    private RiskResponse validateRiskAPIResponse(CloseableHttpResponse httpResponse, PXContext pxContext) {
        StatusLine httpStatus = httpResponse.getStatusLine();

        if (httpStatus.getStatusCode() != 200) {
            handleUnexpectedHttpStatusError(pxContext, httpStatus);
            return null;
        }

        try {
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            if (s.equals("null")) {
                throw new PXException("Risk API returned null JSON");
            }
            logger.debug("Risk API Response: {}", s);
            return JsonUtils.riskResponseReader.readValue(s);
        } catch (Exception e) {
            handleException(pxContext, e, S2SErrorReason.INVALID_RESPONSE, httpResponse.getStatusLine());
        }
        return null;
    }

    private void handleUnexpectedHttpStatusError(PXContext pxContext, StatusLine httpStatus) {
        S2SErrorReason errorReason = S2SErrorReason.UNKNOWN_ERROR;

        int statusCode = httpStatus.getStatusCode();
        String statusMessage = httpStatus.getReasonPhrase();

        if (statusCode >= 500 && statusCode < 600) {
            errorReason = S2SErrorReason.SERVER_ERROR;
        } else if (statusCode >= 400 && statusCode < 500) {
            errorReason = S2SErrorReason.BAD_REQUEST;
        }
        String message = String.format("Risk API returned status %d: %s", statusCode, statusMessage);
        pxContext.setS2sErrorReasonInfo(new S2SErrorReasonInfo(errorReason, message, statusCode, statusMessage));
    }

    private void handleException(PXContext pxContext, Exception e, S2SErrorReason errorReason, StatusLine httpStatusLine) {
        S2SErrorReasonInfo errorReasonInfo = httpStatusLine == null ? new S2SErrorReasonInfo(errorReason, e.toString()) :
                new S2SErrorReasonInfo(errorReason, e.toString(), httpStatusLine.getStatusCode(), httpStatusLine.getReasonPhrase());
        pxContext.setS2sErrorReasonInfo(errorReasonInfo);
        logger.error("Error {}: {}", e.toString(), e.getStackTrace());
    }

    @Override
    public void sendActivity(Activity activity) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(activity);
            logger.debug("Sending Activity: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));

            httpResponse = httpClient.execute(post);
            EntityUtils.consume(httpResponse.getEntity());
        } catch (Exception e) {
            logger.debug("Sending activity failed. Error: {}", e.getMessage());
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    @Override
    public void sendBatchActivities(List<Activity> activities) throws IOException {
        HttpAsyncRequestProducer producer = null;
        BasicAsyncResponseConsumer basicAsyncResponseConsumer = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(activities);
            logger.debug("Sending Activities: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + pxConfiguration.getAuthToken());
            producer = HttpAsyncMethods.create(post);
            basicAsyncResponseConsumer = new BasicAsyncResponseConsumer();
            asyncHttpClient.execute(producer, basicAsyncResponseConsumer, new PxClientAsyncHandler());
        } catch (Exception e) {
            logger.debug("Sending batch activities failed. Error: {}", e.getMessage());
        } finally {
            if (producer != null) {
                producer.close();
            }
            if (basicAsyncResponseConsumer != null) {
                basicAsyncResponseConsumer.close();
            }
        }
    }

    @Override
    public PXDynamicConfiguration getConfigurationFromServer() {
        logger.debug("TimerConfigUpdater[getConfiguration]");
        String queryParams = "";
        if (pxConfiguration.getChecksum() != null) {
            logger.debug("TimerConfigUpdater[getConfiguration]: adding checksum");
            queryParams = "?checksum=" + pxConfiguration.getChecksum();
        }
        PXDynamicConfiguration stub = null;
        HttpGet get = new HttpGet(pxConfiguration.getRemoteConfigurationUrl() + Constants.API_REMOTE_CONFIGURATION + queryParams);

        try (CloseableHttpResponse httpResponse = httpClient.execute(get)) {
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HttpStatus.SC_OK) {
                String bodyContent = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
                stub = JsonUtils.pxConfigurationStubReader.readValue(bodyContent);
                logger.debug("[getConfiguration] GET request successfully executed");
            } else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                logger.debug("[getConfiguration] No updates found");
            } else {
                logger.debug("[getConfiguration] Failed to get remote configuration, status code {}", httpCode);
            }
            return stub;
        } catch (Exception e) {
            logger.error("[getConfiguration] EXCEPTION {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void sendEnforcerTelemetry(EnforcerTelemetry enforcerTelemetry) throws IOException {
        HttpAsyncRequestProducer producer = null;
        BasicAsyncResponseConsumer basicAsyncResponseConsumer = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(enforcerTelemetry);
            logger.debug("Sending enforcer telemetry: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_ENFORCER_TELEMETRY);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            PXCommonUtils.getDefaultHeaders(pxConfiguration.getAuthToken());
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + pxConfiguration.getAuthToken());
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));
            producer = HttpAsyncMethods.create(post);
            basicAsyncResponseConsumer = new BasicAsyncResponseConsumer();
            asyncHttpClient.execute(producer, basicAsyncResponseConsumer, new PxClientAsyncHandler());
        } catch (Exception e) {
            logger.debug("Sending telemetry failed. Error: {}", e.getMessage());
        } finally {
            if (producer != null) {
                producer.close();
            }
            if (basicAsyncResponseConsumer != null) {
                basicAsyncResponseConsumer.close();
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.timerConfigUpdater.close();

        if (this.asyncHttpClient != null) {
            this.asyncHttpClient.close();
        }

        if (this.httpClient != null) {
            this.httpClient.close();
        }
    }
}
