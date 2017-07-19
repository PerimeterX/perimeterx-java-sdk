package com.perimeterx.http;

import com.perimeterx.http.async.PxClientAsyncHandler;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaRequest;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.JsonUtils;
import com.perimeterx.utils.PXCommonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Low level HTTP client
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXHttpClient implements PXClient {

    private static final Logger logger = LoggerFactory.getLogger(PXHttpClient.class);

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
            logger.info("Risk API Request: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_RISK);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration.getConnectionTimeout(),pxConfiguration.getApiTimeout()));

            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            logger.info("Risk API Response: {}", s);
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
            logger.info("Sending Activity: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration.getConnectionTimeout(),pxConfiguration.getApiTimeout()));

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
            asyncHttpClient.start();

            String requestBody = JsonUtils.writer.writeValueAsString(activities);
            logger.info("Sending Activity: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration.getConnectionTimeout(),pxConfiguration.getApiTimeout()));
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

    public CaptchaResponse sendCaptchaRequest(CaptchaRequest captchaRequest) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(captchaRequest);
            logger.info("Sending captcha verification: {}", requestBody);
            HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_CAPTCHA);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration.getConnectionTimeout(),pxConfiguration.getApiTimeout()));

            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            logger.info("Captcha verification response: {}", s);
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
    public PXDynamicConfiguration getConfigurationFromServer() throws IOException{
        logger.debug("TimerConfigUpdater[getConfiguration]");
        CloseableHttpResponse httpResponse = null;
        String queryParams = "";
        if (pxConfiguration.getChecksum() != null) {
            logger.debug("TimerConfigUpdater[getConfiguration]: adding checksum");
            queryParams = "?checksum=" + pxConfiguration.getChecksum();
        }
        PXDynamicConfiguration stub = null;
        try {
            HttpGet get = new HttpGet(Constants.REMOTE_CONFIGURATION_SERVER_URL + Constants.API_REMOTE_CONFIGURATION + queryParams);

            httpResponse = httpClient.execute(get);
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HttpStatus.SC_OK) {
                String bodyContent = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
                stub = JsonUtils.pxConfigurationStubReader.readValue(bodyContent);
                logger.debug("TimerConfigUpdater[getConfiguration] GET request successfully executed {}", bodyContent);
            } else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                logger.debug("TimerConfigUpdater[getConfiguration] No updates found");
            } else {
                logger.debug("TimerConfigUpdater[getConfiguration] Failed to get remote configuration, status code {}", httpCode);
            }
            return stub;
        } catch (Exception e) {
            logger.error("TimerConfigUpdater[getConfiguration] EXCEPTION {}", e.getMessage());
            return null;
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }
}
