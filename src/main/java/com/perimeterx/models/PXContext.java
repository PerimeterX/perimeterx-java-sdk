package com.perimeterx.models;

import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.internals.cookie.PXCookie;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * PXContext - Populate relevant data from HttpRequest
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class PXContext {

    private Map<String,String> pxCookies;
    private String pxCookieOrig;
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
    private String riskCookie;
    private final HttpServletRequest request;
    private final String appId;
    private String blockAction;
    private String cookieHmac;

    public PXContext(final HttpServletRequest request, final IPProvider ipProvider,
                     final HostnameProvider hostnameProvider, final String appId) {
        this.appId = appId;
        initContext(request);
        this.ip = ipProvider.getRequestIP(request);
        this.hostname = hostnameProvider.getHostname(request);
        this.request = request;
    }

    private void initContext(final HttpServletRequest request) {
        this.headers = new HashMap<>();
        final Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String name = (String) headerNames.nextElement();
            final String value = request.getHeader(name);
            this.headers.put(name, value);
        }

        final String cookie = request.getHeader("cookie");
        this.pxCookies = extractPXCookies(cookie);
        final String pxCaptchaCookie = extractCookieByKey(cookie, Constants.COOKIE_CAPTCHA_KEY);
        if (pxCaptchaCookie != null) {
            // Expecting captcha cookie in the form of: token:vid:uuid, vid and uuid may be empty to result in "token::"
            final String[] s = pxCaptchaCookie.split(":", 3);
            if (s.length == 3) {
                this.pxCaptcha = s[0];
                this.vid = s[1];
                this.uuid = s[2];
            } else if (s.length == 1) {
                // To support cookie from an invalid format of "token"
                this.pxCaptcha = s[0];
            }
        }

        this.userAgent = request.getHeader("user-agent");
        this.uri = request.getRequestURI();
        this.fullUrl = request.getRequestURL().toString();
        this.hostname = request.getServerName();
        this.s2sCallReason = S2SCallReason.NONE;
        this.httpMethod = request.getMethod();
        String protocolDetails[] = request.getProtocol().split("/");
        if (protocolDetails.length > 1) {
            this.httpVersion = protocolDetails[1];
        } else {
            this.httpMethod = StringUtils.EMPTY;
        }
    }

    // Prefer to utilize this // throw exception for no cookie found
    private String extractCookieByKey(String cookie, String key) {
        String cookieValue = null;
        if (cookie != null) {
            String[] cookies = cookie.split(";\\s?");
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

  private Map<String,String> extractPXCookies(String cookie) {
        Map<String,String> cookieValue = new HashMap<>();
        if (cookie != null) {
            String[] cookies = cookie.split(";\\s?");
            for (String c : cookies) {
                String[] splicedCookie = c.split("=", 2);
                switch (splicedCookie[0]){
                    case Constants.COOKIE_V1_KEY:
                        cookieValue.put("PXCookieV1", splicedCookie[1]);
                        break;
                    case Constants.COOKIE_V3_KEY:
                        cookieValue.put("PXCookieV3", splicedCookie[1]);
                        break;
                }
            }
        }
        return cookieValue;
    }

    public String getPxCookie() {
        if (pxCookies.isEmpty()){
            return null;
        }
        return pxCookies.containsKey("PXCookieV3") ? pxCookies.get("PXCookieV3") : pxCookies.get("PXCookieV1");
    }

    public Map<String,String> getPxCookies() {
        return pxCookies;
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

    public void setRiskCookie(PXCookie riskCookie) {
        this.riskCookie = riskCookie.getDecodedCookie().toString();
    }

    public String getRiskCookie() {
        return riskCookie;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getAppId() {
        return appId;
    }

    public String getPxCookieOrig() {
        return pxCookieOrig;
    }

    public void setPxCookieOrig(String pxCookieOrig) {
        this.pxCookieOrig = pxCookieOrig;
    }

    public void setBlockAction(String blockAction) {
        this.blockAction = blockAction;
    }

    public void setCookieHmac(String cookieHmac) {
        this.cookieHmac = cookieHmac;
    }

}
