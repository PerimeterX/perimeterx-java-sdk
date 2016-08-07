package com.perimeterx.models;

import com.perimeterx.api.ip.IPProvider;
import com.perimeterx.api.ip.RemoteAddressIPProvider;
import com.perimeterx.internals.cookie.RiskCookie;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PXContext - Populate relevant data from HttpRequest
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class PXContext {

    private String pxCookie;
    private String pxCaptcha;
    private String ip;
    private String vid;
    private String uuid;
    private Map<String, String> headers;
    private String hostname;
    private String uri;
    private String userAgent;
    private String fullUrl;
    private S2SCallReason s2sCallReason;
    private BlockReason blockReason;
    private String httpMethod;
    private String httpVersion;
    private int score;

    public PXContext(final HttpServletRequest request) {
        initContext(request);
        this.ip = new RemoteAddressIPProvider().getRequestIP(request);
    }

    public PXContext(final HttpServletRequest request, final IPProvider ipProvider) {
        initContext(request);
        this.ip = ipProvider.getRequestIP(request);
    }

    private void initContext(final HttpServletRequest request) {
        this.headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        this.headers = Collections.list(headerNames)
                .stream()
                .filter(h -> !headers.containsKey(h))
                .collect(Collectors.toMap(Function.identity(), request::getHeader));
        String cookie = this.headers.get("cookie");
        this.pxCookie = extractCookieByKey(cookie, Constants.COOKIE_KEY);
        String pxCaptchaCookie = extractCookieByKey(cookie, Constants.COOKIE_CAPTCHA_KEY);
        if (pxCaptchaCookie != null) {
            String[] s = pxCaptchaCookie.split(":", 2);
            if (s.length == 2) {
                this.pxCaptcha = s[0];
                this.vid = s[1];
            }
        }

        this.userAgent = this.headers.get("user-agent");
        this.uri = request.getRequestURI();
        this.fullUrl = request.getRequestURL().toString();
        this.hostname = request.getRemoteHost();
        this.s2sCallReason = S2SCallReason.NONE;
        this.httpMethod = request.getMethod();
        this.httpVersion = Optional.ofNullable(request.getProtocol().split("/")[1]).orElse(StringUtils.EMPTY);// extracting only the version number from HTTP/x.x
    }

    // Prefer to utilize this // throw exception for no cookie found
    private String extractCookieByKey(String cookie, String key) {
        String cookieValue = null;
        if (cookie != null) {
            String[] cookies = cookie.split("; ");
            for (String c : cookies) {
                String[] splicedCookie = c.split("=", 2);
                if (key.equals(splicedCookie[0])) {
                    cookieValue = splicedCookie[1];
                    break;
                }
            }
        }
        return cookieValue;
    }

    public String getPxCookie() {
        return pxCookie;
    }

    public String getPxCaptcha() {
        return pxCaptcha;
    }

    public String getIp() {
        return ip;
    }

    public String getVid() {
        return vid;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUri() {
        return uri;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public S2SCallReason getS2sCallReason() {
        return s2sCallReason;
    }

    public void setS2sCallReason(S2SCallReason callReason) {
        this.s2sCallReason = callReason;
    }

    public BlockReason getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(BlockReason blockReason) {
        this.blockReason = blockReason;
    }

    public String getUuid() {
        return uuid;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void addCookieDetails(RiskCookie riskCookie) {
        this.vid = riskCookie.vid;
        this.uuid = riskCookie.uuid;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getScore() {
        return this.score;
    }
}
