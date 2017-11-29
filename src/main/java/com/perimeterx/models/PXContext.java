package com.perimeterx.models;

import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.BlockAction;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.perimeterx.utils.Constants.*;

/**
 * PXContext - Populate relevant data from HttpRequest
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
public class PXContext {

    private static final PXLogger logger = PXLogger.getLogger(PXContext.class);

    /**
     * Original HTTP request
     */
    private final HttpServletRequest request;

    /**
     * PerimeterX cookies - _px$cookie_version$, _pxCaptcha.
     */
    private Map<String, String> pxCookies;

    /**
     * Original _px cookie
     */
    private String pxCookieOrig;

    /**
     * Original Captcha cookie
     */
    private String pxCaptcha;

    /**
     * Request IP as extracted with IPProvider.
     *
     * @see com.perimeterx.api.providers.IPProvider#getRequestIP(HttpServletRequest)
     */
    private String ip;

    // Additional fields extracted from the original HTTP request
    private String vid;
    private String uuid;
    private Map<String, String> headers;
    private String hostname;
    private String uri;
    private String userAgent;
    private String fullUrl;
    private String httpMethod;
    private String httpVersion;

    // PerimeterX computed data on the request
    private String riskCookie;
    private final String appId;

    private String cookieHmac;

    /**
     * Score for the current request - if riskScore is above configured {@link com.perimeterx.models.configuration.PXConfiguration#blockingScore} on
     * PXConfiguration then the {@link com.perimeterx.models.PXContext#verified} is set to false
     */
    private int riskScore;

    /**
     * Reason for calling PX Service
     *
     * @see com.perimeterx.models.risk.S2SCallReason
     */
    private S2SCallReason s2sCallReason;

    private boolean madeS2SApiCall;
    /**
     * Which action to take after being blocked
     */
    private BlockAction blockAction;

    /**
     * if true - calling risk_api to verified request even if cookie data is valid
     */
    private boolean sensitiveRoute;

    /**
     * Reason for request being verified
     * @see com.perimeterx.models.risk.PassReason
     */
    private PassReason passReason;

    /**
     * Risk api timing
     */
    private long riskRtt;

    /**
     * Request verification status - if {@link com.perimeterx.models.PXContext#verified} is true, the request is safe to pass to server.
     */
    private boolean verified;

    /**
     * Reason for why request should be blocked - relevant when request is not verified, meaning - verified {@link com.perimeterx.models.PXContext#verified} is false
     */
    private BlockReason blockReason;
    private String blockActionData;
    private boolean isMobileToken;

    public PXContext(final HttpServletRequest request, final IPProvider ipProvider, final HostnameProvider hostnameProvider, PXConfiguration pxConfiguration) {
        this.appId = pxConfiguration.getAppId();
        initContext(request, pxConfiguration);
        this.ip = ipProvider.getRequestIP(request);
        this.hostname = hostnameProvider.getHostname(request);
        this.request = request;
    }

    private void initContext(final HttpServletRequest request, PXConfiguration pxConfiguration) {
        this.headers = getHeadersFromRequest(request);

        if (headers.containsKey(MOBILE_SDK_HEADER)) {
            logger.debug(PXLogger.LogReason.DEBUG_MOBILE_SDK_DETECTED);
            this.isMobileToken = true;
        }

        this.pxCookieOrig = isMobileToken ? ORIGIN_HEADER : ORIGIN_COOKIE;

        //Get cookies
        final String cookie = request.getHeader(isMobileToken ? MOBILE_SDK_HEADER : ORIGIN_COOKIE);
        this.pxCookies = isMobileToken ? extractPXMobileCookie(cookie) : extractPXCookies(cookie);
        this.pxCaptcha = extractCookieByKey(cookie, Constants.COOKIE_CAPTCHA_KEY);
        this.userAgent = request.getHeader("user-agent");
        this.uri = request.getRequestURI();
        this.fullUrl = request.getRequestURL().toString();
        this.s2sCallReason = S2SCallReason.NONE;
        this.blockReason = BlockReason.NONE;
        this.passReason = PassReason.NONE;
        this.madeS2SApiCall = false;
        this.riskRtt = 0;
        this.httpMethod = request.getMethod();

        String protocolDetails[] = request.getProtocol().split("/");
        if (protocolDetails.length > 1) {
            this.httpVersion = protocolDetails[1];
        } else {
            this.httpMethod = StringUtils.EMPTY;
        }

        this.sensitiveRoute = checkSensitiveRoute(pxConfiguration.getSensitiveRoutes(), uri);
    }

    private Map<String, String> getHeadersFromRequest(HttpServletRequest request) {
        HashMap<String, String> headers = new HashMap<>();
        String name;
        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            name = (String) headerNames.nextElement();
            headers.put(name.toLowerCase(), request.getHeader(name));
        }
        return headers;
    }

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

    private Map<String, String> extractPXMobileCookie(String cookieString) {
        Map<String, String> cookieMap = new HashMap<>();
        String[] cookieParts;
        String cookieFirstPart;
        String cookieVersion;

        if (cookieString != null && !cookieString.isEmpty()) {
            cookieParts = cookieString.split(COOKIE_EXTRACT_DELIMITER_MOBILE, 2);
            cookieFirstPart = cookieParts[0];

            //Mobile Error
            if (cookieParts.length == 1 && AbstractPXCookie.isMobileErrorCode(cookieFirstPart)) {
                cookieMap.put(cookieFirstPart, cookieFirstPart);
            }
            //Mobile version & cookie
            else if (cookieParts.length == 2) {
                cookieVersion = AbstractPXCookie.convertMobileCookieVersion(cookieParts[0]);
                if (!cookieVersion.isEmpty()) {
                    cookieMap.put(cookieVersion, cookieParts[1]);
                }
            }
        }
        return cookieMap;
    }

    private Map<String, String> extractPXCookies(String cookie) {
        Map<String, String> cookieValue = new HashMap<>();

        if (cookie != null) {
            String[] cookies = cookie.split(";\\s?");
            for (String c : cookies) {
                String[] splicedCookie = c.split(COOKIE_EXTRACT_DELIMITER_WEB, 2);
                switch (splicedCookie[0]) {
                    case Constants.COOKIE_V1_KEY_PREFIX:
                        cookieValue.put(Constants.COOKIE_V1_KEY_PREFIX, splicedCookie[1]);

                        break;
                    case Constants.COOKIE_V3_KEY_PREFIX:
                        cookieValue.put(Constants.COOKIE_V3_KEY_PREFIX, splicedCookie[1]);
                        break;
                }
            }
        }
        return cookieValue;
    }

    public String getPxCookie() {
        if (pxCookies.isEmpty()) {
            return null;
        }
        return pxCookies.containsKey(Constants.COOKIE_V3_KEY_PREFIX) ? pxCookies.get(Constants.COOKIE_V3_KEY_PREFIX) : pxCookies.get(Constants.COOKIE_V1_KEY_PREFIX);
    }

    public String getCookieVersion() {
        return pxCookies.isEmpty() ? null : (pxCookies.containsKey(Constants.COOKIE_V3_KEY_PREFIX)? Constants.COOKIE_V3_KEY_PREFIX : Constants.COOKIE_V1_KEY_PREFIX);
    }

    public Map<String, String> getPxCookies() {
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

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getRiskScore() {
        return this.riskScore;
    }

    public void setRiskCookie(AbstractPXCookie riskCookie) {
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

    public PassReason getPassReason() {
        return this.passReason;
    }

    public void setPassReason(PassReason passReason) {
        this.passReason = passReason;
    }

    public void setPxCookieOrig(String pxCookieOrig) {
        this.pxCookieOrig = pxCookieOrig;
    }

    public void setBlockAction(String blockAction) {
        switch (blockAction) {
            case Constants.ACTION_CAPTCHA:
                this.blockAction = BlockAction.CAPTCHA;
                break;
            case Constants.ACTION_BLOCK:
                this.blockAction = BlockAction.BLOCK;
                break;
            case Constants.ACTION_CHALLENGE:
                this.blockAction = BlockAction.CHALLENGE;
                break;
            default:
                this.blockAction = BlockAction.CAPTCHA;
                break;
        }
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public void setCookieHmac(String cookieHmac) {
        this.cookieHmac = cookieHmac;
    }


    public String getCookieHmac() {
        return this.cookieHmac;
    }

    public boolean isSensitiveRoute(){
        return this.sensitiveRoute;
    }

    public long getRiskRtt() {
        return this.riskRtt;
    }

    public void setRiskRtt(long riskRtt) {
        this.riskRtt = riskRtt;
    }

    /**
     * Check if request is verified or not
     *
     * @return true if request is valid, false otherwise
     */
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    private boolean checkSensitiveRoute(Set<String> sensitiveRoutes, String uri) {
        for (String sensitiveRoutePrefix : sensitiveRoutes) {
            if (uri.startsWith(sensitiveRoutePrefix)) {
                return true;
            }
        }
        return false;
    }

    public void setMadeS2SApiCall(boolean flag) {
        this.madeS2SApiCall = flag;
    }

    public boolean isMadeS2SApiCall(){
        return this.madeS2SApiCall;
    }

    public String getBlockActionData() {
        return blockActionData;
    }

    public void setBlockActionData(String blockActionData) {
        this.blockActionData = blockActionData;
    }

    public boolean isMobileToken() {
        return isMobileToken;
    }

    public String getCollectorURL() {
        return String.format("%s%s%s", Constants.API_COLLECTOR_PREFIX, appId, Constants.API_COLLECTOR_POSTFIX);
    }
}
