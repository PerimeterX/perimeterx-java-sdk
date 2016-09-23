package com.perimeterx.http;

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
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Low level HTTP client
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
public class PXHttpClient implements PXClient {

    private static PXHttpClient instance;
    private static final Charset UTF_8 = Charset.forName("utf-8");

    private CloseableHttpClient httpClient;
    private String authToken;
    private String baseUrl;

    public static PXHttpClient getInstance(String baseUrl, int timeout, String authToken) {
        if (instance == null) {
            synchronized (PXHttpClient.class) {
                if (instance == null) {
                    instance = new PXHttpClient(baseUrl, timeout, authToken);
                }
            }
        }
        return instance;
    }

    protected PXHttpClient(String baseUrl, int timeout, String authToken) {
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .build();
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(config)
                .build();
    }

    @Override
    public RiskResponse riskApiCall(RiskRequest riskRequest) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(riskRequest);
            HttpPost post = new HttpPost(baseUrl + Constants.API_RISK);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setHeader("Authorization", "Bearer " + authToken);
            post.setHeader("Content-Type", "application/json");
            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            return JsonUtils.riskResponseReader.readValue(s);
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
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(activity);
            HttpPost post = new HttpPost(baseUrl + Constants.API_ACTIVITIES);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setHeader("Authorization", "Bearer " + authToken);
            post.setHeader("Content-Type", "application/json");
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

    public CaptchaResponse sendCaptchaRequest(CaptchaRequest captchaRequest) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(captchaRequest);
            HttpPost post = new HttpPost(baseUrl + Constants.API_CAPTCHA);
            post.setEntity(new StringEntity(requestBody, UTF_8));
            post.setHeader("Authorization", "Bearer " + authToken);
            post.setHeader("Content-Type", "application/json");
            httpResponse = httpClient.execute(post);
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            return JsonUtils.captchaResponseReader.readValue(s);
        } catch (Exception e) {
            throw new PXException(e);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }
}
