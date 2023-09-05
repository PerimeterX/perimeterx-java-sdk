package com.perimeterx.http;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.perimeterx.utils.PXLogger;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Low level HTTP client
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXHttpClient implements PXClient, Closeable {

    private static final PXLogger logger = PXLogger.getLogger(PXHttpClient.class);
    private final IPXHttpClient client;
    private final PXConfiguration pxConfiguration;


    public PXHttpClient(PXConfiguration pxConfiguration) throws PXException {
        try {
            this.pxConfiguration = pxConfiguration;
            if (pxConfiguration.getHttpClient() == null) {
                this.client = new PXApacheHttpClient(pxConfiguration);
            } else {
                this.client = pxConfiguration.getHttpClient();
            }
        } catch (Exception e) {
            throw new PXException(e);
        }
    }


    @Override
    public RiskResponse riskApiCall(PXContext pxContext) throws IOException {
        IPXIncomingResponse httpResponse = null;
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

    private IPXIncomingResponse executeRiskAPICall(String requestBody, PXContext pxContext) throws ConnectTimeoutException {
        IPXOutgoingRequest request = PXOutgoingRequestImpl.builder()
                .url(this.pxConfiguration.getServerURL() + Constants.API_RISK)
                .httpMethod(PXHttpMethod.POST)
                .stringBody(requestBody)
                .build();

        try {
            return client.send(request);

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

    private RiskResponse validateRiskAPIResponse(IPXIncomingResponse httpResponse, PXContext pxContext) {
        PXHttpStatus httpStatus = httpResponse.status();

        if (httpStatus.getStatusCode() != 200) {
            handleUnexpectedHttpStatusError(pxContext, httpStatus);
            return null;
        }

        try {
            String s = IOUtils.toString(httpResponse.body(), UTF_8);
            if (s.equals("null")) {
                throw new PXException("Risk API returned null JSON");
            }
            logger.debug("Risk API Response: {}", s);
            return JsonUtils.riskResponseReader.readValue(s);
        } catch (Exception e) {
            handleException(pxContext, e, S2SErrorReason.INVALID_RESPONSE, httpResponse.status());
        }
        return null;
    }

    private void handleUnexpectedHttpStatusError(PXContext pxContext, PXHttpStatus httpStatus) {
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

    private void handleException(PXContext pxContext, Exception e, S2SErrorReason errorReason, PXHttpStatus httpStatusLine) {
        S2SErrorReasonInfo errorReasonInfo = httpStatusLine == null ? new S2SErrorReasonInfo(errorReason, e.toString()) :
                new S2SErrorReasonInfo(errorReason, e.toString(), httpStatusLine.getStatusCode(), httpStatusLine.getReasonPhrase());
        pxContext.setS2sErrorReasonInfo(errorReasonInfo);
        logger.error("Error {}: {}", e.toString(), e.getStackTrace());
    }

    @Override
    public void sendActivity(Activity activity) throws IOException {
        IPXIncomingResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(activity);
            logger.debug("Sending Activity: {}", requestBody);
            IPXOutgoingRequest request = PXOutgoingRequestImpl.builder()
                    .stringBody(requestBody)
                    .url(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES)
                    .build();
            httpResponse = client.send(request);
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
        String requestBody = JsonUtils.writer.writeValueAsString(activities);
        logger.debug("Sending Activities: {}", requestBody);
        IPXOutgoingRequest request = PXOutgoingRequestImpl.builder()
                .url(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES)
                .httpMethod(PXHttpMethod.POST)
                .stringBody(requestBody)
                .header(new PXHttpHeader(HttpHeaders.CONTENT_TYPE, "application/json"))
                .header(new PXHttpHeader(HttpHeaders.AUTHORIZATION, "Bearer " + pxConfiguration.getAuthToken()))
                .build();
        client.sendAsync(request);
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
        IPXOutgoingRequest request = PXOutgoingRequestImpl.builder()
                .url(pxConfiguration.getRemoteConfigurationUrl() + Constants.API_REMOTE_CONFIGURATION + queryParams)
                .httpMethod(PXHttpMethod.GET)
                .build();
        try (IPXIncomingResponse httpResponse = client.send(request)) {
            int httpCode = httpResponse.status().getStatusCode();
            if (httpCode == HttpStatus.SC_OK) {
                String bodyContent = IOUtils.toString(httpResponse.body(), UTF_8);
                stub = JsonUtils.pxConfigurationStubReader.readValue(bodyContent);
                logger.debug("[getConfiguration] GET request successfully executed");
            } else if (httpResponse.status().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
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
        String requestBody = JsonUtils.writer.writeValueAsString(enforcerTelemetry);
        logger.debug("Sending enforcer telemetry: {}", requestBody);
        IPXOutgoingRequest request = PXOutgoingRequestImpl.builder()
                .url(this.pxConfiguration.getServerURL() + Constants.API_ENFORCER_TELEMETRY)
                .httpMethod(PXHttpMethod.POST)
                .stringBody(requestBody)
                .header(new PXHttpHeader(HttpHeaders.CONTENT_TYPE, "application/json"))
                .header((new PXHttpHeader(HttpHeaders.AUTHORIZATION, "Bearer " + pxConfiguration.getAuthToken())))
                .build();
        client.send(request);
    }

    @Override
    public void close() throws IOException {
        if (this.client != null) {
            this.client.close();
        }
    }
}
