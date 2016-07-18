package com.perimeterx.http;

import com.fasterxml.jackson.databind.ObjectReader;
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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
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
        return callPXServer(riskRequest, JsonUtils.riskResponseReader, this.baseUrl + Constants.API_RISK);
    }

    @Override
    public void sendActivity(Activity activity) throws PXException, IOException {
        callPXServer(activity, null, baseUrl + Constants.API_ACTIVITIES);
    }

    public CaptchaResponse sendCaptchaRequest(CaptchaRequest captchaRequest) throws PXException, IOException {
        return callPXServer(captchaRequest, JsonUtils.captchaResponseReader, baseUrl + Constants.API_CAPTCHA);
    }


    /**
     * Convenience method to utilize  server call when making a request to PX server
     * When calling with {@link ObjectReader} equals to null - nothing will be parsed when returning from server
     * and the entity will be consumed just to prevent connection leak
     *
     * @param request - server request
     * @param reader  - type of {@link ObjectReader} to parse response back to POJO, if called with null -
     *                response will not be parsed back to object and method will return null
     * @param route   - PX server API endpoint
     * @param <T>     - type of request
     * @param <E>     - type of response
     * @return - server response parsed back to POJO of type type E
     * @throws PXException
     * @throws IOException
     */
    private <T, E> E callPXServer(final T request, final ObjectReader reader, final String route) throws PXException, IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            String requestBody = JsonUtils.writer.writeValueAsString(request);
            HttpUriRequest httpRequest = RequestBuilder
                    .post(route)
                    .setEntity(new StringEntity(requestBody, UTF_8))
                    .setHeader("Authorization", "Bearer " + authToken)
                    .setHeader("Content-Type", "application/json")
                    .build();
            httpResponse = httpClient.execute(httpRequest);
            // If returned server response is not needed
            if (reader == null) {
                EntityUtils.consume(httpResponse.getEntity());
                return null;
            }
            String s = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
            return reader.readValue(s);
        } catch (Exception e) {
            throw new PXException(e);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }
}
