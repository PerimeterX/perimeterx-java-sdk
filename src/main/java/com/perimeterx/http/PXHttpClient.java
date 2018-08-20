package com.perimeterx.http;

import com.perimeterx.http.async.PxClientAsyncHandler;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.EnforcerTelemetry;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.ResetCaptchaRequest;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.JsonUtils;
import com.perimeterx.utils.PXCommonUtils;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Low level HTTP client
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXHttpClient implements PXClient {

    private static final PXLogger logger = PXLogger.getLogger(PXHttpClient.class);

    private static PXHttpClient instance;
    private static final Charset UTF_8 = Charset.forName("utf-8");

    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient asyncHttpClient;

    private PXConfiguration pxConfiguration;

    public static PXHttpClient getInstance(PXConfiguration pxConfiguration, CloseableHttpAsyncClient asyncHttpClient, CloseableHttpClient httpClient) {
        if (instance == null) {
            synchronized (PXHttpClient.class) {
                if (instance == null) {
                    instance = new PXHttpClient(pxConfiguration, asyncHttpClient, httpClient);
                }
            }
        }
        return instance;
    }


    private PXHttpClient(PXConfiguration pxConfiguration, CloseableHttpAsyncClient asyncHttpClient, CloseableHttpClient httpClient) {
        this.pxConfiguration = pxConfiguration;
        this.httpClient = httpClient;
        this.asyncHttpClient = asyncHttpClient;
    }

    @Override
    public RiskResponse riskApiCall(RiskRequest riskRequest) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(riskRequest);
            logger.debug("Risk API Request: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_RISK);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));

            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            logger.debug("Risk API Response: {}", s);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return JsonUtils.riskResponseReader.readValue(s);
            }
            return null;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    @Override
    public void sendActivity(Activity activity) throws PXException, IOException {
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
            throw new PXException(e);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    @Override
    public void sendBatchActivities(List<Activity> activities) throws PXException, IOException {
        HttpAsyncRequestProducer producer = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(activities);
            logger.debug("Sending Activity: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + pxConfiguration.getAuthToken());
            producer = HttpAsyncMethods.create(post);
            asyncHttpClient.execute(producer, new BasicAsyncResponseConsumer(), new PxClientAsyncHandler());
        } catch (Exception e) {
            throw new PXException(e);
        } finally {
            if (producer != null) {
                producer.close();
            }
        }
    }

    public CaptchaResponse sendCaptchaRequest(ResetCaptchaRequest resetCaptchaRequest) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(resetCaptchaRequest);
            logger.debug("Sending captcha verification: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_CAPTCHA);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));

            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            logger.debug("Captcha verification response: {}", s);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return JsonUtils.captchaResponseReader.readValue(s);
            }
            return null;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
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

        try (CloseableHttpResponse httpResponse = httpClient.execute(get)){
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HttpStatus.SC_OK) {
                String bodyContent = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
                stub = JsonUtils.pxConfigurationStubReader.readValue(bodyContent);
                logger.debug("[getConfiguration] GET request successfully executed {}", bodyContent);
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
    public void sendEnforcerTelemetry(EnforcerTelemetry enforcerTelemetry) throws PXException, IOException{
        HttpAsyncRequestProducer producer = null;
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
            asyncHttpClient.execute(producer, new BasicAsyncResponseConsumer(), new PxClientAsyncHandler());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (producer != null) {
                producer.close();
            }
        }
    }
}
