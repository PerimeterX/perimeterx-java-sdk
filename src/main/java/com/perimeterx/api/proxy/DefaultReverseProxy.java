package com.perimeterx.api.proxy;

import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.http.IPXHttpClient;
import com.perimeterx.http.IPXOutgoingRequest;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.proxy.PredefinedResponse;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXLogger;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by nitzangoldfeder on 14/05/2018.
 */
public class DefaultReverseProxy implements ReverseProxy {

    private final PXLogger logger = PXLogger.getLogger(DefaultReverseProxy.class);
    private final String DEFAULT_JAVASCRIPT_VALUE = "";
    private final String DEFAULT_JSON_VALUE = "{}";
    private final byte[] DEFAULT_EMPTY_GIF_VALUE = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, (byte) 0x80, 0x00,
            0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x21, (byte) 0xf9, 0x04,
            0x01, 0x0a, 0x00, 0x01, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x4c, 0x01, 0x00, 0x3b};
    private final String CONTENT_TYPE_JAVASCRIPT = "application/javascript";
    private final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private final String CONTENT_TYPE_IMAGE_GIF = "image/gif";

    private final String XHR_PATH = "xhr";
    private final String CLIENT_FP_PATH = "init.js";
    private final String CLIENT_TP_PATH = "main.min.js";
    private final String CAPTACHA_PATH = "captcha";

    private IPProvider ipProvider;
    private String clientPath;
    private String clientReversePrefix;
    private String xhrReversePrefix;
    private String captchaReversePrefix;
    private String collectorUrl;
    private IPXHttpClient proxyClient;
    private PredefinedResponseHelper predefinedResponseHelper;

    private PXConfiguration pxConfiguration;

    public DefaultReverseProxy(PXConfiguration pxConfiguration, IPProvider ipProvider) {
        this.predefinedResponseHelper = new DefaultPredefinedResponseHandler();
        this.pxConfiguration = pxConfiguration;
        String reverseAppId = pxConfiguration.getAppId().substring(2);
        this.clientReversePrefix = String.format("/%s/%s", reverseAppId, CLIENT_FP_PATH);
        this.xhrReversePrefix = String.format("/%s/%s", reverseAppId, XHR_PATH);
        this.captchaReversePrefix = "/" + reverseAppId + "/" + CAPTACHA_PATH;
        this.clientPath = String.format("/%s/%s", pxConfiguration.getAppId(), CLIENT_TP_PATH);
        this.collectorUrl = pxConfiguration.getCollectorUrl();
        this.ipProvider = ipProvider;

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(pxConfiguration.getMaxConnections());
        cm.setDefaultMaxPerRoute(pxConfiguration.getMaxConnectionsPerRoute());
        this.proxyClient = pxConfiguration.getIPXHttpClientInstance();
    }

    public boolean reversePxClient(HttpServletRequest req, HttpServletResponse res) throws URISyntaxException, IOException {
        if (!req.getRequestURI().startsWith(clientReversePrefix)) {
            return false;
        }

        if (!pxConfiguration.isFirstPartyEnabled()) {
            logger.debug("First party is disabled, rendering default response");
            PredefinedResponse predefinedResponse = new PredefinedResponse(CONTENT_TYPE_JAVASCRIPT, DEFAULT_JAVASCRIPT_VALUE);
            predefinedResponseHelper.handlePredefinedResponse(res, predefinedResponse);
            return true;
        }

        String url = "https://" + pxConfiguration.getClientHost();

        RemoteServer remoteServer = new RemoteServer(url, clientPath, req, res, ipProvider, proxyClient, null, null, pxConfiguration);
        IPXOutgoingRequest proxyRequest = remoteServer.prepareProxyRequest();
        remoteServer.handleResponse(proxyRequest, false);
        return true;
    }

    public boolean reversePxXhr(HttpServletRequest req, HttpServletResponse res) throws URISyntaxException, IOException {
        if (!req.getRequestURI().startsWith(xhrReversePrefix)) {
            return false;
        }

        String predefinedContent = DEFAULT_JSON_VALUE;
        String predefinedContentType = CONTENT_TYPE_APPLICATION_JSON;

        // If uri ends with gif
        if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf(".") + 1).equalsIgnoreCase("gif")) {
            predefinedContent = new String(DEFAULT_EMPTY_GIF_VALUE);
            predefinedContentType = CONTENT_TYPE_IMAGE_GIF;
        }

        PredefinedResponse predefinedResponse = new PredefinedResponse(predefinedContentType, predefinedContent);

        if (!pxConfiguration.isFirstPartyEnabled() || !pxConfiguration.isXhrFirstPartyEnabled()) {
            logger.debug("First party is disabled, rendering default response");
            predefinedResponseHelper.handlePredefinedResponse(res, predefinedResponse);
            return true;
        }

        final String originalUrl = req.getRequestURI().substring(xhrReversePrefix.length());
        final RemoteServer remoteServer = new RemoteServer(collectorUrl, originalUrl, req, res, ipProvider, proxyClient, predefinedResponse, predefinedResponseHelper, pxConfiguration);
        IPXOutgoingRequest proxyRequest = null;

        try {
            proxyRequest = remoteServer.prepareProxyRequest();
            remoteServer.handleResponse(proxyRequest, true);
        } catch (Exception e) {
            logger.error("reversePxXhr - failed to handle xhr request, error :: ", e.getMessage());
            safelyCloseInputStream(proxyRequest);

            throw e;
        }

        return true;
    }

    private void safelyCloseInputStream(IPXOutgoingRequest proxyRequest) throws IOException {
        final boolean inputStreamExist = proxyRequest != null && proxyRequest.getBody() != null && proxyRequest.getBody().getInputStream() != null;
        if (inputStreamExist) {
            proxyRequest.getBody().getInputStream().close();
        }
    }

    @Override
    public boolean reverseCaptcha(HttpServletRequest req, HttpServletResponseWrapper res) throws IOException, URISyntaxException {
        if (!req.getRequestURI().contains(captchaReversePrefix)) {
            return false;
        }
        if (!pxConfiguration.isFirstPartyEnabled()) {
            logger.debug("First party is disabled, rendering default response");
            PredefinedResponse predefinedResponse = new PredefinedResponse(CONTENT_TYPE_JAVASCRIPT, DEFAULT_JAVASCRIPT_VALUE);
            predefinedResponseHelper.handlePredefinedResponse(res, predefinedResponse);
            return false;
        }
        String query = req.getQueryString();
        String originalRequest = pxConfiguration.getAppId() + "/captcha.js?" + query;
        String url = "https://" + Constants.CAPTCHA_HOST + "/" + originalRequest;
        logger.debug("Forwarding request from " + captchaReversePrefix + "/" + originalRequest + "to xhr at " + url);

        RemoteServer remoteServer = new RemoteServer("", url, req, res, ipProvider, proxyClient, null, predefinedResponseHelper, pxConfiguration);
        IPXOutgoingRequest proxyRequest = remoteServer.prepareProxyRequest();
        remoteServer.handleResponse(proxyRequest, true);
        return true;

    }

    public void setIpProvider(IPProvider ipProvider) {
        this.ipProvider = ipProvider;
    }

    public void setPredefinedResponseHelper(PredefinedResponseHelper predefinedResponseHelper) {
        this.predefinedResponseHelper = predefinedResponseHelper;
    }

    public void setProxyClient(IPXHttpClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    public void destroy() {
        if (proxyClient instanceof Closeable) {
            try {
                ((Closeable) proxyClient).close();
            } catch (IOException e) {
                logger.debug("While destroying servlet, shutting down HttpClient: " + e, e);
            }
        }
    }
}
