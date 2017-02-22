package com.perimeterx.http;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaRequest;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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
    private AsyncHttpClient asyncHttpClient;
    private String authToken;
    private String baseUrl;
    private List<Activity> activitiesBuffer;
    private PXConfiguration pxConfiguration;

    public static PXHttpClient getInstance(PXConfiguration pxConfiguration) {
        if (instance == null) {
            synchronized (PXHttpClient.class) {
                if (instance == null) {
                    instance = new PXHttpClient(pxConfiguration);
                }
            }
        }
        return instance;
    }


    private PXHttpClient(PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
        this.baseUrl = this.pxConfiguration.getServerURL();
        this.authToken = this.pxConfiguration.getAuthToken();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(this.pxConfiguration.getApiTimeout())
                .build();
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(config)
                .build();
        activitiesBuffer = new ArrayList<>();
        asyncHttpClient = new DefaultAsyncHttpClient();

    }

    @Override
    public RiskResponse riskApiCall(RiskRequest riskRequest) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(riskRequest);
            logger.info("Risk API Request: {}", requestBody);
            HttpPost post = new HttpPost(baseUrl + Constants.API_RISK);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setHeader("Authorization", "Bearer " + authToken);
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
        Future<Response> httpAsyncResponse = null;
        try {
            // Add to activities buffer
            activitiesBuffer.add(activity);
            // Send activities only if buffer limit was reached
            if (activitiesBuffer.size() == this.pxConfiguration.getMaxBufferLen()){
                String requestBody = JsonUtils.writer.writeValueAsString(activitiesBuffer);
                logger.info("Sending Activity: {}", requestBody);
                BoundRequestBuilder asyncPost = asyncHttpClient.preparePost(baseUrl + Constants.API_ACTIVITIES);
                asyncPost.setBody(requestBody);
                asyncPost.setHeader("Authorization","Bearer " + authToken);
                asyncPost.setHeader("Content-Type", "application/json");
                asyncPost.execute( new PXHttpClientAsyncHandler() );
                //Empty buffer
                activitiesBuffer.clear();
            }
        } catch (Exception e) {
            throw new PXException(e);
        }
    }

    public CaptchaResponse sendCaptchaRequest(CaptchaRequest captchaRequest) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(captchaRequest);
            logger.info("Sending captcha verification: {}", requestBody);
            HttpPost post = new HttpPost(baseUrl + Constants.API_CAPTCHA);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setHeader("Authorization", "Bearer " + authToken);
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
}
