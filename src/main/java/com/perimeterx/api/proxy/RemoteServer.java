package com.perimeterx.api.proxy;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.http.*;
import com.perimeterx.http.PXOutgoingRequestImpl.PXOutgoingRequestImplBuilder;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.proxy.PredefinedResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Objects;

import static com.perimeterx.utils.Constants.FIRST_PARTY_HEADER_NAME;
import static com.perimeterx.utils.Constants.FIRST_PARTY_HEADER_VALUE;
import static com.perimeterx.utils.PXIOUtils.copy;

/**
 * Created by nitzangoldfeder on 14/05/2018.
 */
public class RemoteServer {

    private final PXContext context;
    private HttpServletResponse res;
    private HttpServletRequest req;
    private IPXHttpClient proxyClient;
    private IPProvider ipProvider;
    private int maxUrlLength = 1000;
    private PredefinedResponse predefinedResponse;
    private PredefinedResponseHelper predefinedResponseHelper;
    private PXConfiguration pxConfiguration;

    protected String targetUri;
    protected URI targetUriObj;
    protected HttpHost targetHost;

    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set&lt;String&gt; because this
     * approach does case insensitive lookup faster.
     */
    protected static final HeaderGroup hopByHopHeaders;

    static {
        hopByHopHeaders = new HeaderGroup();
        String[] headers = new String[]{
                "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
                "TE", "Trailers", "Transfer-Encoding", "Upgrade"};
        for (String header : headers) {
            hopByHopHeaders.addHeader(new BasicHeader(header, null));
        }
    }

    public RemoteServer(String serverUrl, HttpServletRequest req, HttpServletResponse res,
                        IPProvider ipProvider, IPXHttpClient httpClient, PredefinedResponse predefinedResponse,
                        PredefinedResponseHelper predefinedResponseHelper, PXConfiguration pxConfiguration, PXContext context) throws URISyntaxException {
        this.req = req;
        this.res = res;
        this.targetUri = serverUrl;
        this.proxyClient = httpClient;
        this.targetUriObj = new URI(targetUri);
        this.targetHost = URIUtils.extractHost(targetUriObj);
        this.ipProvider = ipProvider;
        this.predefinedResponse = predefinedResponse;
        this.predefinedResponseHelper = predefinedResponseHelper;
        this.pxConfiguration = pxConfiguration;
        this.context = context;
    }

    public IPXOutgoingRequest prepareProxyRequest() throws IOException {
        context.logger.debug("Preparing first party request");
        String method = req.getMethod();
        String proxyRequestUri = rewriteUrlFromRequest(req);

        PXOutgoingRequestImplBuilder requestBuilder = PXOutgoingRequestImpl.builder();
        // Copy the body if content-length exists
        if (getContentLength(req) != -1) {
            requestBuilder.body(new PXRequestBody(req.getInputStream()));
        }

        if (!Objects.equals(method, "")) {
            requestBuilder.httpMethod(PXHttpMethod.valueOf(method));
        }
        requestBuilder.url(proxyRequestUri);
        // Reverse proxy
        copyRequestHeaders(req, requestBuilder);
        handleXForwardedForHeader(req, requestBuilder);

        // PX Logic
        handlePXHeaders(requestBuilder);

        return requestBuilder.build();
    }

    public IPXIncomingResponse handleResponse(IPXOutgoingRequest proxyRequest, PXContext context) {
        IPXIncomingResponse proxyResponse = null;
        try {
            // Execute the request
            proxyResponse = doExecute(proxyRequest);
            int statusCode = proxyResponse.status().getStatusCode();

            // In failure we can check if we enable predefined request or proxy the original response
            if (this.isAllowedPredefinedResponse() && statusCode >= HttpStatus.SC_BAD_REQUEST) {
                predefinedResponseHelper.handlePredefinedResponse(res, predefinedResponse, context);
                return proxyResponse;
            }

            res.setStatus(statusCode);

            // Copying response headers to make sure SESSIONID or other Cookie which comes from the remote
            // server will be saved in client when the proxied url was redirected to another one.
            // See issue [#51](https://github.com/mitre/HTTP-Proxy-Servlet/issues/51)
            copyResponseHeaders(proxyResponse, req, res);

            if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
                // 304 needs special handling.  See:
                // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
                // Don't send body entity/content!
                res.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
            } else {
                // Send the content to the client
                copyResponseEntity(proxyResponse);
            }

        } catch (Exception e) {
            if (this.isAllowedPredefinedResponse()) {
                predefinedResponseHelper.handlePredefinedResponse(res, predefinedResponse, context);
            }
        }
        return proxyResponse;
    }

    /**
     * Copy response body data (the entity) from the proxy to the servlet client.
     */
    protected void copyResponseEntity(IPXIncomingResponse proxyResponse) throws IOException {
        InputStream body = proxyResponse.body();
        if (body != null) {
            copy(body, res.getOutputStream());
        }
    }

    /**
     * Copy proxied response headers back to the servlet client.
     */
    protected void copyResponseHeaders(IPXIncomingResponse proxyResponse, HttpServletRequest servletRequest,
                                       HttpServletResponse servletResponse) {
        for (PXHttpHeader header : proxyResponse.headers()) {
            copyResponseHeader(servletRequest, servletResponse, header);
        }
    }

    /**
     * Copy a proxied response header back to the servlet client.
     * This is easily overwritten to filter out certain headers if desired.
     */
    protected void copyResponseHeader(HttpServletRequest servletRequest,
                                      HttpServletResponse servletResponse, PXHttpHeader header) {
        String headerName = header.getName();
        if (hopByHopHeaders.containsHeader(headerName))
            return;
        String headerValue = header.getValue();
        if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE) ||
                headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE2)) {
            copyProxyCookie(servletRequest, servletResponse, headerValue);
        } else if (headerName.equalsIgnoreCase(HttpHeaders.LOCATION)) {
            // LOCATION Header may have to be rewritten.
            servletResponse.addHeader(headerName, rewriteUrlFromResponse(servletRequest, headerValue));
        } else {
            servletResponse.addHeader(headerName, headerValue);
        }
    }

    /**
     * For a redirect response from the target server, this translates {@code theUrl} to redirect to
     * and translates it to one the original client can use.
     */
    protected String rewriteUrlFromResponse(HttpServletRequest servletRequest, String theUrl) {
        final String targetUri = this.targetUri;
        if (theUrl.startsWith(targetUri)) {
            /*-
             * The URL points back to the back-end server.
             * Instead of returning it verbatim we replace the target path with our
             * source path in a way that should instruct the original client to
             * request the URL pointed through this Proxy.
             * We do this by taking the current request and rewriting the path part
             * using this servlet's absolute path and the path from the returned URL
             * after the base target URL.
             */
            StringBuffer curUrl = servletRequest.getRequestURL();//no query
            int pos;
            // Skip the protocol part
            if ((pos = curUrl.indexOf("://")) >= 0) {
                // Skip the authority part
                // + 3 to skip the separator between protocol and authority
                if ((pos = curUrl.indexOf("/", pos + 3)) >= 0) {
                    // Trim everything after the authority part.
                    curUrl.setLength(pos);
                }
            }
            // Context path starts with a / if it is not blank
            curUrl.append(servletRequest.getContextPath());
            // Servlet path starts with a / if it is not blank
            curUrl.append(servletRequest.getServletPath());
            curUrl.append(theUrl, targetUri.length(), theUrl.length());
            return curUrl.toString();
        }
        return theUrl;
    }

    /**
     * Copy cookie from the proxy to the servlet client.
     * Replaces cookie path to local path and renames cookie to avoid collisions.
     */
    protected void copyProxyCookie(HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse, String headerValue) {
        //build path for resulting cookie
        String path = servletRequest.getContextPath(); // path starts with / or is empty string
        path += servletRequest.getServletPath(); // servlet path starts with / or is empty string
        if (path.isEmpty()) {
            path = "/";
        }

        for (HttpCookie cookie : HttpCookie.parse(headerValue)) {
            //set cookie name prefixed w/ a proxy value so it won't collide w/ other cookies
            String proxyCookieName = cookie.getName();
            Cookie servletCookie = new Cookie(proxyCookieName, cookie.getValue());
            servletCookie.setComment(cookie.getComment());
            servletCookie.setMaxAge((int) cookie.getMaxAge());
            servletCookie.setPath(path); //set to the path of the proxy servlet
            // don't set cookie domain
            servletCookie.setSecure(cookie.getSecure());
            servletCookie.setVersion(cookie.getVersion());
            servletResponse.addCookie(servletCookie);
        }
    }

    /**
     * Copy request headers from the servlet client to the proxy request.
     * This is easily overridden to add your own.
     */
    protected void copyRequestHeaders(HttpServletRequest servletRequest, PXOutgoingRequestImplBuilder requestBuilder) {
        // Get an Enumeration of all of the header names sent by the client
        Enumeration<String> enumerationOfHeaderNames = servletRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = enumerationOfHeaderNames.nextElement();
            copyRequestHeader(servletRequest, requestBuilder, headerName);
        }
    }

    /**
     * Append request headers related to PerimeterX
     */
    protected void handlePXHeaders(PXOutgoingRequestImplBuilder proxyRequest) {
        proxyRequest.header(new PXHttpHeader("X-PX-ENFORCER-TRUE-IP", this.ipProvider.getRequestIP(this.req)));
        proxyRequest.header(new PXHttpHeader(FIRST_PARTY_HEADER_NAME, FIRST_PARTY_HEADER_VALUE));
    }

    private void handleXForwardedForHeader(HttpServletRequest servletRequest, PXOutgoingRequestImplBuilder proxyRequest) {
        String forHeaderName = "X-Forwarded-For";
        String forHeader = servletRequest.getRemoteAddr();
        String existingForHeader = servletRequest.getHeader(forHeaderName);
        if (existingForHeader != null) {
            forHeader = existingForHeader + ", " + forHeader;
        }
        proxyRequest.header(new PXHttpHeader(forHeaderName, forHeader));

        String protoHeaderName = "X-Forwarded-Proto";
        String protoHeader = servletRequest.getScheme();
        proxyRequest.header(new PXHttpHeader(protoHeaderName, protoHeader));
    }

    /**
     * Copy a request header from the servlet client to the proxy request.
     * This is easily overridden to filter out certain headers if desired.
     */
    protected void copyRequestHeader(HttpServletRequest servletRequest, PXOutgoingRequestImplBuilder proxyRequest,
                                     String headerName) {
        //Instead the content-length is effectively set via InputStreamEntity
        if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
            return;
        }

        if (hopByHopHeaders.containsHeader(headerName)) {
            return;
        }

        if (pxConfiguration.getIpHeaders().contains(headerName)) {
            return;
        }

        Enumeration<String> headers = servletRequest.getHeaders(headerName);
        while (headers.hasMoreElements()) {//sometimes more than one value
            String headerValue = headers.nextElement();
            if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                HttpHost host = this.targetHost;
                headerValue = host.getHostName();
                if (host.getPort() != -1) {
                    headerValue += ":" + host.getPort();
                }
            }
            proxyRequest.header(new PXHttpHeader(headerName, headerValue));
        }
    }

    // Get the header value as a long in order to more correctly proxy very large requests
    private long getContentLength(HttpServletRequest request) {
        return request.getContentLength();
    }
    protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
        context.logger.debug("Rewiring url from request");
        StringBuilder uri = new StringBuilder(this.maxUrlLength);
        uri.append(this.targetUri);

        context.logger.debug("Setting uri to reverse {}", uri);
        // Handle the query string & fragment
        String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
        String fragment = null;
        //split off fragment from queryString, updating queryString if found
        if (queryString != null) {
            int fragIdx = queryString.indexOf('#');
            if (fragIdx >= 0) {
                fragment = queryString.substring(fragIdx + 1);
                queryString = queryString.substring(0, fragIdx);
            }
        }

        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            // queryString is not decoded, so we need encodeUriQuery not to encode "%" characters, to avoid double-encoding
            uri.append(encodeUriQuery(queryString, false));
        }

        if (fragment != null) {
            uri.append('#');
            // fragment is not decoded, so we need encodeUriQuery not to encode "%" characters, to avoid double-encoding
            uri.append(encodeUriQuery(fragment, false));
        }
        context.logger.debug("Final uri to proxy: {}", uri);
        return uri.toString();
    }

    /**
     * Encodes characters in the query or fragment part of the URI.
     *
     * <p>Unfortunately, an incoming URI sometimes has characters disallowed by the spec.  HttpClient
     * insists that the outgoing proxied request has a valid URI because it uses Java's {@link URI}.
     * To be more forgiving, we must escape the problematic characters.  See the URI class for the
     * spec.
     *
     * @param in            example: name=value&amp;foo=bar#fragment
     * @param encodePercent determine whether percent characters need to be encoded
     */
    private static CharSequence encodeUriQuery(CharSequence in, boolean encodePercent) {
        //Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
        StringBuilder outBuf = null;
        Formatter formatter = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = true;
            if (c < 128) {
                if (asciiQueryChars.get((int) c) && !(encodePercent && c == '%')) {
                    escape = false;
                }
            } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {//not-ascii
                escape = false;
            }
            if (!escape) {
                if (outBuf != null)
                    outBuf.append(c);
            } else {
                //escape
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 5 * 3);
                    outBuf.append(in, 0, i);
                    formatter = new Formatter(outBuf);
                }
                //leading %, 0 padded, width 2, capital hex
                formatter.format("%%%02X", (int) c);
            }
        }
        return outBuf != null ? outBuf : in;
    }

    private static final BitSet asciiQueryChars;

    static {
        char[] c_unreserved = "_-!.~'()*".toCharArray();//plus alphanum
        char[] c_punct = ",;:$&+=".toCharArray();
        char[] c_reserved = "?/[]@".toCharArray();//plus punct

        asciiQueryChars = new BitSet(128);
        for (char c = 'a'; c <= 'z'; c++) asciiQueryChars.set((int) c);
        for (char c = 'A'; c <= 'Z'; c++) asciiQueryChars.set((int) c);
        for (char c = '0'; c <= '9'; c++) asciiQueryChars.set((int) c);
        for (char c : c_unreserved) asciiQueryChars.set((int) c);
        for (char c : c_punct) asciiQueryChars.set((int) c);
        for (char c : c_reserved) asciiQueryChars.set((int) c);

        asciiQueryChars.set((int) '%');//leave existing percent escapes in place
    }

    protected IPXIncomingResponse doExecute(IPXOutgoingRequest proxyRequest) throws IOException {
        return proxyClient.send(proxyRequest);
    }

    private boolean isAllowedPredefinedResponse() {
        return this.predefinedResponse != null && this.predefinedResponseHelper != null;
    }
}
