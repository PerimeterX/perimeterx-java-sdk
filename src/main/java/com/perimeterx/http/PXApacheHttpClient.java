package com.perimeterx.http;

import com.perimeterx.http.async.PxClientAsyncHandler;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.PXCommonUtils;
import com.perimeterx.utils.PXLogger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static com.perimeterx.utils.Constants.URL_HTTPS_PREFIX;


public class PXApacheHttpClient implements IPXHttpClient {
    private static final PXLogger logger = PXLogger.getLogger(PXApacheHttpClient.class);
    private static final int INACTIVITY_PERIOD_TIME_MS = 1000;
    private static final long MAX_IDLE_TIME_SEC = 30L;
    private final PXConfiguration pxConfiguration;
    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient asyncHttpClient;
    private TimerValidateRequestsQueue timerConfigUpdater;

    public PXApacheHttpClient(PXConfiguration pxConfiguration) {
        this(pxConfiguration, null, null);
    }

    public PXApacheHttpClient(PXConfiguration pxConfiguration,
                              CloseableHttpClient httpClient,
                              CloseableHttpAsyncClient asyncHttpClient) {
        this.pxConfiguration = pxConfiguration;
        this.httpClient = httpClient;
        this.asyncHttpClient = asyncHttpClient;
        if (this.httpClient == null) {
            initHttpClient();
        }
        if (this.asyncHttpClient == null) {
            initAsyncHttpClient();
        }
    }

    @Override
    public IPXIncomingResponse send(IPXOutgoingRequest request) throws IOException {
        HttpUriRequest apacheRequest = createRequest(request);
        CloseableHttpResponse response = httpClient.execute(apacheRequest);
        return new PXApacheIncomingResponse(response);
    }

    @Override
    public void sendAsync(IPXOutgoingRequest request) throws IOException {
        HttpAsyncRequestProducer producer = null;
        BasicAsyncResponseConsumer basicAsyncResponseConsumer = null;
        try {
            HttpUriRequest apacheRequest = createRequest(request);
            producer = HttpAsyncMethods.create(apacheRequest);
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
    public void close() throws IOException {
        if (this.timerConfigUpdater != null) {
            this.timerConfigUpdater.close();
        }

        if (this.asyncHttpClient != null) {
            this.asyncHttpClient.close();
        }

        if (this.httpClient != null) {
            this.httpClient.close();
        }
    }

    private void initHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(this.pxConfiguration.getMaxConnections());
        cm.setDefaultMaxPerRoute(this.pxConfiguration.getMaxConnectionsPerRoute());
        cm.setValidateAfterInactivity(INACTIVITY_PERIOD_TIME_MS);

        this.httpClient = HttpClients.custom()
                .evictExpiredConnections()
                .evictIdleConnections(MAX_IDLE_TIME_SEC, TimeUnit.SECONDS)
                .setConnectionManager(cm)
                .build();
    }

    private void initAsyncHttpClient() {
        try {
            DefaultConnectingIOReactor ioReactor = getDefaultConnectingIOReactor();
            PoolingNHttpClientConnectionManager nHttpConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
            CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
                    .setConnectionManager(nHttpConnectionManager)
                    .build();
            closeableHttpAsyncClient.start();
            asyncHttpClient = closeableHttpAsyncClient;
            this.timerConfigUpdater = new TimerValidateRequestsQueue(nHttpConnectionManager, pxConfiguration);
            timerConfigUpdater.schedule();
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }
    }

    private static DefaultConnectingIOReactor getDefaultConnectingIOReactor() throws IOReactorException {
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
        return ioReactor;
    }


    private HttpRequestBase createRequest(IPXOutgoingRequest request) {
        HttpRequestBase req = buildBaseRequest(request);

        try {
            req.setURI(new URI(request.getUrl()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        for (PXHttpHeader header : request.getHeaders()) {
            req.addHeader(header.getName(), header.getValue());
        }

        if (!isValidFirstPartyRequest(req.getURI())) {
            req.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));
        }

        return req;
    }

    private HttpRequestBase buildBaseRequest(IPXOutgoingRequest request) {
        PXRequestBody body = request.getBody();
        if (body != null && body.getInputStream() != null) {
            HttpEntityEnclosingRequestBase req = new HttpEntityEnclosingRequestBase() {
                @Override
                public String getMethod() {
                    return request.getHttpMethod().name();
                }
            };
            req.setEntity(new InputStreamEntity(body.getInputStream()));
            return req;
        } else {
            return new HttpRequestBase() {
                @Override
                public String getMethod() {
                    return request.getHttpMethod().name();
                }
            };
        }
    }

    private boolean isValidFirstPartyRequest(URI uri) {
        final String url = URL_HTTPS_PREFIX + uri.getHost() + uri.getPath();
        final String captchaURL = pxConfiguration.getCaptchaURL();
        final String sensorURL = pxConfiguration.getSensorURL();
        final String xhrURL = pxConfiguration.getXhrUrl(uri.getPath(), false);

        return url.equals(captchaURL) ||
                url.equals(sensorURL) ||
                url.equals(xhrURL);
    }
}
