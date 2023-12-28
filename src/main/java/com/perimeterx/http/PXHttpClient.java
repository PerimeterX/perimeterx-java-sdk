package com.perimeterx.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.PXOutgoingRequestImpl.PXOutgoingRequestImplBuilder;
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
import com.perimeterx.utils.logger.IPXLogger;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicHeader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Low level HTTP client
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXHttpClient implements PXClient, Closeable {

    private static final IPXLogger logger = PerimeterX.globalLogger;
    private final IPXHttpClient client;
    private final PXConfiguration pxConfiguration;


    public PXHttpClient(PXConfiguration pxConfiguration) throws PXException {
        try {
            this.pxConfiguration = pxConfiguration;
            this.client = pxConfiguration.getIPXHttpClientInstance();
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
            pxContext.logger.debug("Risk API Request: {}", requestBody);
            return requestBody;
        } catch (JsonProcessingException e) {
            handleException(pxContext, e, S2SErrorReason.UNABLE_TO_SEND_REQUEST, null);
            return null;
        }
    }

    private IPXIncomingResponse executeRiskAPICall(String requestBody, PXContext pxContext) throws ConnectTimeoutException {
        IPXOutgoingRequest request = buildOutgoingRequest(this.pxConfiguration.getServerURL() + Constants.API_RISK,PXHttpMethod.POST, requestBody);
        try {
            return client.send(request);

        } catch (ConnectTimeoutException e) {
            pxContext.logger.debug("ConnectTimeoutException", e);
            throw e;
        } catch (SocketTimeoutException e) {
            pxContext.logger.debug("SocketTimeoutException", e);
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

        try (InputStream inputStream = httpResponse.body()) {
            String s = IOUtils.toString(inputStream, UTF_8);
            if (s.equals("null")) {
                throw new PXException("Risk API returned null JSON");
            }
            pxContext.logger.debug("Risk API Response: {}", s);
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
        pxContext.logger.error("Error {}: {}", e.toString(), e.getStackTrace());
    }

    @Override
    public void sendActivity(Activity activity, PXContext context) throws IOException {
        IPXIncomingResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(activity);
            context.logger.debug("Sending Activity: {}", requestBody);
            IPXOutgoingRequest request = buildOutgoingRequest(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES,PXHttpMethod.POST, requestBody);
            httpResponse = client.send(request);
        } catch (Exception e) {
            context.logger.debug("Sending activity failed. Error: {}", e.getMessage());
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    @Override
    public void sendBatchActivities(List<Activity> activities, PXContext context) throws IOException {
        String requestBody = JsonUtils.writer.writeValueAsString(activities);
        context.logger.debug("Sending Activities: {}", requestBody);
        IPXOutgoingRequest request = buildOutgoingRequest(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES,PXHttpMethod.POST, requestBody);
        client.sendAsync(request, context);
    }

    @Override
    public void sendLogs(String activities, PXContext context) throws IOException {
        context.logger.debug("Sending logs to logging service");
        BasicHeader loggerAuthTokenHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + pxConfiguration.getLoggerAuthToken());
        String url = this.pxConfiguration.getServerURL() + Constants.API_LOGGING_SERVICE_PATH;
        IPXOutgoingRequest request = buildOutgoingRequest(url,PXHttpMethod.POST, activities,loggerAuthTokenHeader);
        client.sendAsync(request,context);
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
        IPXOutgoingRequest request = buildOutgoingRequest(pxConfiguration.getRemoteConfigurationUrl() + Constants.API_REMOTE_CONFIGURATION + queryParams
                ,PXHttpMethod.GET,
                "");
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
    public void sendEnforcerTelemetry(EnforcerTelemetry enforcerTelemetry, PXContext context) throws IOException {
        String requestBody = JsonUtils.writer.writeValueAsString(enforcerTelemetry);
        if (context!=null){
            context.logger.debug("Sending enforcer telemetry: {}", requestBody);
        } else{
            logger.debug("Sending enforcer telemetry: {}", requestBody);
        }
        IPXOutgoingRequest request = buildOutgoingRequest(this.pxConfiguration.getServerURL() + Constants.API_ENFORCER_TELEMETRY,PXHttpMethod.POST, requestBody);
        client.send(request);
    }

    private IPXOutgoingRequest buildOutgoingRequest(String url , PXHttpMethod method, String requestBody, BasicHeader... headers) {
        List<PXHttpHeader> pxHeaders = getPxHttpHeaders(headers);
        PXOutgoingRequestImplBuilder requestBuilder = PXOutgoingRequestImpl.builder()
                .headers(pxHeaders)
                .url(url)
                .httpMethod(method)
                .stringBody(requestBody);

        if (method == PXHttpMethod.POST) {
            requestBuilder.stringBody(requestBody);
        }
        return requestBuilder.build();
    }


    @Override
    public void close() throws IOException {
        if (this.client != null) {
            this.client.close();
        }
    }


    private List<PXHttpHeader> getPxHttpHeaders(BasicHeader... headers) {
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(HttpHeaders.CONTENT_TYPE, "application/json");
        headersMap.put(HttpHeaders.AUTHORIZATION, "Bearer " + pxConfiguration.getAuthToken());

        for (BasicHeader header: headers) {
            headersMap.put(header.getName(),header.getValue());
        }
        PXOutgoingRequestImplBuilder builder = PXOutgoingRequestImpl.builder();
        List<PXHttpHeader> headersList = new ArrayList<>();
        headersMap.forEach((name, value) ->{
            headersList.add(new PXHttpHeader(name,value));
//            builder.header(new PXHttpHeader(name,value));
        });

        return headersList;
    }
}
