package com.perimeterx.http;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.http.async.PxClientAsyncHandler;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaRequest;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Low level HTTP client
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXHttpClient implements PXClient {

    Logger logger = LoggerFactory.getLogger(PXHttpClient.class);

    private static PXHttpClient instance;
    private static final Charset UTF_8 = Charset.forName("utf-8");

    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient asyncHttpClient;
    private List<Activity> activitiesBuffer;
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
        this.activitiesBuffer = new ArrayList<>();
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
            post.setHeader("Authorization", "Bearer " + this.pxConfiguration.getAuthToken());
            post.setHeader("Content-Type", "application/json");
            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            logger.info("Risk API Response: {}", s);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return JsonUtils.riskResponseReader.readValue(s);
            }
            return null;
        } catch (Exception e) {
            throw new PXException(e);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    @Override
    public void sendActivity(Activity activity) throws PXException, IOException {
        HttpAsyncRequestProducer producer = null;
        try {
            // Add to activities buffer
            activitiesBuffer.add(activity);
            // Send activities only if buffer limit was reached
            if (activitiesBuffer.size() == this.pxConfiguration.getMaxBufferLen()) {
                // Prepare headers
                asyncHttpClient.start();

                // Prepare body
                String requestBody = JsonUtils.writer.writeValueAsString(activitiesBuffer);
                logger.info("Sending Activity: {}", requestBody);

                // Build request
                HttpPost post = new HttpPost(this.pxConfiguration.getServerURL() + Constants.API_ACTIVITIES);
                post.setEntity(new StringEntity(requestBody, UTF_8));
                post.setHeader("Authorization", "Bearer " + this.pxConfiguration.getAuthToken());
                post.setHeader("Content-Type", "application/json");

                // Execute
                producer = HttpAsyncMethods.create(post);

                asyncHttpClient.execute(producer, new BasicAsyncResponseConsumer(), new PxClientAsyncHandler());
                //Empty buffer
                activitiesBuffer.clear();
            }
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
            post.setHeader("Authorization", "Bearer " + this.pxConfiguration.getAuthToken());
            post.setHeader("Content-Type", "application/json");
            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            logger.info("Captcha verification response: {}", s);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return JsonUtils.captchaResponseReader.readValue(s);
            }
            return null;
        } catch (Exception e) {
            throw new PXException(e);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    public List<Activity> getActivitiesBuffer() {
        return activitiesBuffer;
    }
}
