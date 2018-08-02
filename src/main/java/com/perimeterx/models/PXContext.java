package com.perimeterx.models;

import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.CustomParameters;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.S2SCallReason;
import com.perimeterx.utils.BlockAction;
import com.perimeterx.utils.Constants;
import com.perimeterx.utils.PXCommonUtils;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     * Indicates to the pxCookieFactory which cookie to create, the current or the original
     * */
    private boolean deserializeFromOriginalToken = false;

    /**
     * PerimeterX cookies - _px$cookie_version$, _pxCaptcha.
     */
    private Map<String,String> pxCookies;

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

    private PXConfiguration pxConfiguration;

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

    /**
     * Contains the data that would be rendered on the response when score exceeds the threshold
     */
    private String blockActionData;

    /**
     * Marks is the origin of the request comes from mobile client
     */
    private boolean isMobileToken;

    /**
     * Marks the origin of the pxCookie
     */
    private String cookieOrigin = Constants.COOKIE_ORIGIN;

    /**
     * Custom parameters from the requests, cusotm parameters are set via {@link com.perimeterx.api.providers.CustomParametersProvider#buildCustomParameters(PXConfiguration, PXContext)}
     * if exist, the custom parameters will be set when risk api is triggered
     */
    private CustomParameters customParameters;

    /**
     * Simmilar to {@link com.perimeterx.models.PXContext#verified}, flags if to continue the filter chain or return
     * This will be set to true when the module will handle the request though proxy mode
     */
    private boolean firstPartyRequest;


    /**
     * The original uuid of the request.
     * */
    private String originalUuid;
    /**
     * The original token decoded.
     * */
    private String decodedOriginalToken;
    /**
     * Errors encountered during the creation process of the cookie
     * */
    private String originalTokenError;
    /**
     * The original token cookies.
     * */
    private Map<String, String> originalTokenCookies;
    /**
     * The original token sent from the mobile sdk, prior to the last request received.
     * */
    private String originalToken;

    public PXContext(final HttpServletRequest request, final IPProvider ipProvider, final HostnameProvider hostnameProvider, PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
        logger.debug(PXLogger.LogReason.DEBUG_REQUEST_CONTEXT_CREATED);
        this.appId = pxConfiguration.getAppId();
        initContext(request, pxConfiguration);
        this.ip = ipProvider.getRequestIP(request);
        this.hostname = hostnameProvider.getHostname(request);
        this.request = request;
    }

    private void initContext(final HttpServletRequest request, PXConfiguration pxConfiguration) {
        this.headers = PXCommonUtils.getHeadersFromRequest(request);

        if (headers.containsKey(Constants.MOBILE_SDK_HEADER)) {
            logger.debug(PXLogger.LogReason.DEBUG_MOBILE_SDK_DETECTED);
            this.isMobileToken = true;
            this.cookieOrigin = Constants.HEADER_ORIGIN;
        }
        String cookie = isMobileToken ? request.getHeader(Constants.MOBILE_SDK_HEADER) : request.getHeader(Constants.COOKIE_ORIGIN);
        extractCookies(request, cookie);
        this.pxCookieOrig = getPxCookie();
        final String pxCaptchaCookie = extractCookieByKey(cookie, Constants.COOKIE_CAPTCHA_KEY);
        if (pxCaptchaCookie != null) {
            this.pxCaptcha = pxCaptchaCookie;
        }

        this.firstPartyRequest = false;
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

    private void extractCookies(HttpServletRequest request, String cookie) {
        if (isMobileToken){
            this.pxCookies = extractPXMobileCookie(cookie);
            this.originalToken = extractOriginalToken(request);
            if (this.originalToken != null){
                this.originalTokenCookies = extractPXMobileCookie(this.originalToken);
            }
        }
        else{
            this.pxCookies = extractPXCookies(cookie);
        }
    }

    private String extractOriginalToken(HttpServletRequest request) {
        String originalCookie = null;
        if (headers.containsKey(Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER)){
            originalCookie = request.getHeader(Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER);
        }
        return originalCookie;
    }

    public String getPxOriginalTokenCookie() {
        String pxOriginalTokenCookie = null;
        if (originalTokenCookies != null) {
            pxOriginalTokenCookie = originalTokenCookies.containsKey(Constants.COOKIE_V3_KEY) ? originalTokenCookies.get(Constants.COOKIE_V3_KEY) : originalTokenCookies.get(Constants.COOKIE_V1_KEY);
        }
        return pxOriginalTokenCookie;
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

    private Map<String, String> extractPXCookies(String cookie) {
        Map<String, String> cookieValue = new HashMap<>();
        if (cookie != null) {
            String[] cookies = cookie.split(";\\s?");
            for (String c : cookies) {
                String[] splicedCookie = c.split("=", 2);
                switch (splicedCookie[0]) {
                    case Constants.COOKIE_V1_KEY:
                        cookieValue.put(Constants.COOKIE_V1_KEY, splicedCookie[1]);

                        break;
                    case Constants.COOKIE_V3_KEY:
                        cookieValue.put(Constants.COOKIE_V3_KEY, splicedCookie[1]);
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
            cookieParts = cookieString.split(Constants.COOKIE_EXTRACT_DELIMITER_MOBILE, 2);
            cookieFirstPart = cookieParts[0];

            //Mobile Error
            if (cookieParts.length == 1) {
                cookieMap.put(Constants.COOKIE_V3_KEY, cookieFirstPart);
            }
            //Mobile cookie
            else if (cookieParts.length == 2) {
                cookieVersion = AbstractPXCookie.getMobileCookieVersion(cookieParts[0]);
                cookieMap.put(cookieVersion, cookieParts[1]);
            }
        }

        return cookieMap;
    }

    public String getPxCookie() {
        return pxCookies.containsKey(Constants.COOKIE_V3_KEY) ? pxCookies.get(Constants.COOKIE_V3_KEY) : pxCookies.get(Constants.COOKIE_V1_KEY);
    }

    public String getCookieVersion() {
        return pxCookies.isEmpty() ? null : (pxCookies.containsKey(Constants.COOKIE_V3_KEY)? Constants.COOKIE_V3_KEY: Constants.COOKIE_V1_KEY);
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
            case Constants.CAPTCHA_ACTION_CAPTCHA:
                this.blockAction = BlockAction.CAPTCHA;
                break;
            case Constants.BLOCK_ACTION_CAPTCHA:
                this.blockAction = BlockAction.BLOCK;
                break;
            case Constants.BLOCK_ACTION_CHALLENGE:
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
     * @deprecated - Use {@link PXContext#isHandledResponse}
     * @return true if request is valid, false otherwise
     */
    @Deprecated
    public boolean isVerified() {
        return verified;
    }

    /**
     * Check if request is verified or not, this method should not be used as a condition if to pass the request to
     * the application, instead use {@link PXContext#isHandledResponse} for the reason that its not
     * handling a case where we already responded if this is a first party request
     *
     * The {@link PXContext#isRequestLowScore} only indicates if the request was verified and should
     * called only if more details about the request is needed (like knowing the reason why shouldn't be passed)
     *
     * @return true if request is valid, false otherwise
     */
    public boolean isRequestLowScore() {
        return verified;
    }

    /**
     * Check if PerimeterX already handled the response thus the request should not be passed to the application
     * In case true, you can check if {@link PXContext#isRequestLowScore()} or {@link PXContext#isFirstPartyRequest()}
     * for more information if the response was handled by first party or score was lower than the configured threshold
     *
     * @return true if response was handled
     */
    public boolean isHandledResponse() {
        return !verified || firstPartyRequest;
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

    public String getCookieOrigin() {
        return cookieOrigin;
    }

    public boolean isMobileToken() {
        return isMobileToken;
    }

    public String getCollectorURL() {
        return String.format("%s%s%s", Constants.API_COLLECTOR_PREFIX, appId, Constants.API_COLLECTOR_POSTFIX);
    }

    public CustomParameters getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(CustomParameters customParameters) {
        this.customParameters = customParameters;
    }

    /**
     * Check if PerimeterX treated the request as first partyta,
     * @return true if response was hadled by PerimeterX module
     */
    public boolean isFirstPartyRequest() {
        return firstPartyRequest;
    }

    public void setFirstPartyRequest(boolean firstPartyRequest) {
        this.firstPartyRequest = firstPartyRequest;
    }

    public PXConfiguration getPxConfiguration() {
        return pxConfiguration;
    }


    public void setOriginalTokenError(String originalTokenError){
        this.originalTokenError = originalTokenError;
    }

    public void setDeserializeFromOriginalToken(boolean deserializeFromOriginalToken){
        this.deserializeFromOriginalToken = deserializeFromOriginalToken;
    }

    public boolean shouldDeserializeFromOriginalToken() {
        return deserializeFromOriginalToken;
    }


    public void setDecodedOriginalToken(String decodedOriginalToken) {
        this.decodedOriginalToken = decodedOriginalToken;
    }

    public void setOriginalUuid(String originalUuid) {
        this.originalUuid = originalUuid;
    }

    public Map<String, String> getOriginalTokenCookies() {
        return originalTokenCookies;
    }

    public String getOriginalUuid() {
        return originalUuid;
    }

    public String getOriginalTokenError() {
        return originalTokenError;
    }

    public String getDecodedOriginalToken() {
        return decodedOriginalToken;
    }

    public String getOriginalToken() {
        return originalToken;
    }
}
