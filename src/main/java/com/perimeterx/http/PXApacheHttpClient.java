package com.perimeterx.http;

import com.perimeterx.http.async.PxClientAsyncHandler;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.PXCommonUtils;
import com.perimeterx.utils.PXLogger;
import org.apache.http.client.methods.*;
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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PXApacheHttpClient implements IPXHttpClient {
    private static final PXLogger logger = PXLogger.getLogger(PXApacheHttpClient.class);
    private static final int INACTIVITY_PERIOD_TIME_MS = 1000;
    private static final long MAX_IDLE_TIME_SEC = 30L;
    private final PXConfiguration pxConfiguration;
    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient asyncHttpClient;
    private TimerValidateRequestsQueue timerConfigUpdater;

    public PXApacheHttpClient(PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
    }

    @Override
    public void init() throws IOException {
        initHttpClient();
        initAsyncHttpClient();
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
                .setDefaultHeaders(PXCommonUtils.getDefaultHeaders(pxConfiguration.getAuthToken()))
                .build();
    }

    private void initAsyncHttpClient() throws IOReactorException {
        DefaultConnectingIOReactor ioReactor = getDefaultConnectingIOReactor();

        PoolingNHttpClientConnectionManager nHttpConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
        CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(nHttpConnectionManager)
                .build();
        closeableHttpAsyncClient.start();
        asyncHttpClient = closeableHttpAsyncClient;
        this.timerConfigUpdater = new TimerValidateRequestsQueue(nHttpConnectionManager, pxConfiguration);
        timerConfigUpdater.schedule();
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


        for (PXHttpHeader header : request.getHeaders()) {
            req.addHeader(header.getName(), header.getValue());
        }
        req.setConfig(PXCommonUtils.getRequestConfig(pxConfiguration));
        return req;
    }

    private HttpRequestBase createGetRequest(IPXOutgoingRequest request) {
        return new HttpGet(request.getUrl());

    }

    private HttpRequestBase createPostRequest(IPXOutgoingRequest request) {
        HttpPost post = new HttpPost(request.getUrl());
        post.setEntity(new StringEntity(request.getBody(), UTF_8));
        return post;
    }

    private HttpRequestBase buildBaseRequest(IPXOutgoingRequest request) {
        switch (request.getHttpMethod()) {
            case POST:
                return createPostRequest(request);
            case GET:
                return createGetRequest(request);
            default:
                throw new IllegalArgumentException("unsupported method " + request.getHttpMethod());
        }
    }

}
