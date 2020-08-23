package com.perimeterx.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.api.providers.CustomParametersProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.IPProvider;
import com.perimeterx.internals.cookie.AbstractPXCookie;
import com.perimeterx.internals.cookie.DataEnrichmentCookie;
import com.perimeterx.internals.cookie.RawCookieData;
import com.perimeterx.internals.cookie.cookieparsers.CookieHeaderParser;
import com.perimeterx.internals.cookie.cookieparsers.HeaderParser;
import com.perimeterx.internals.cookie.cookieparsers.MobileCookieHeaderParser;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.risk.BlockReason;
import com.perimeterx.models.risk.CustomParameters;
import com.perimeterx.models.risk.PassReason;
import com.perimeterx.models.risk.VidSource;
import com.perimeterx.utils.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PXContext - Populate relevant data from HttpRequest
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
@Data
public class PXContext {

    private static final PXLogger logger = PXLogger.getLogger(PXContext.class);

    /**
     * Original HTTP request
     */
    private final HttpServletRequest request;

    private String pxCookieRaw;

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
     * Score for the current request - if riskScore is above configured blockingScore on
     * PXConfiguration then the {@link com.perimeterx.models.PXContext#verified} is set to false
     */
    private int riskScore;

    /**
     * Reason for calling PX Service
     *
     * @see com.perimeterx.models.risk.S2SCallReason
     */
    private String s2sCallReason;

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
     *
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
    private String cookieOrigin = Constants.COOKIE_HEADER_NAME;

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
     */
    private String originalUuid;
    /**
     * The original token decoded.
     */
    private String decodedOriginalToken;
    /**
     * Errors encountered during the creation process of the cookie
     */
    private String originalTokenError;
    /**
     * The original token cookie.
     */
    private String originalTokenCookie;

    /**
     * The risk mode (monitor / active_blocking) of the request
     */
    private String riskMode;

    private List<RawCookieData> tokens;
    private List<RawCookieData> originalTokens;
    private String cookieVersion;

    /**
     * PerimeterX data enrichment cookie payload
     */
    private JsonNode pxde;
    private boolean pxdeVerified = false;

    /**
     * All the names of the cookies in the request
     */
    private String[] requestCookieNames;

    /**
     * the source of the vid
     */
    private VidSource vidSource = VidSource.NONE;
    /**
     * the pxhd cookie
     */

    private String pxhd;

    private String responsePxhd;

    public PXContext(final HttpServletRequest request, final IPProvider ipProvider, final HostnameProvider hostnameProvider, PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
        logger.debug(PXLogger.LogReason.DEBUG_REQUEST_CONTEXT_CREATED);
        this.appId = pxConfiguration.getAppId();
        this.ip = ipProvider.getRequestIP(request);
        this.hostname = hostnameProvider.getHostname(request);
        this.request = request;
        initContext(request, pxConfiguration);
    }

    private void initContext(final HttpServletRequest request, PXConfiguration pxConfiguration) {
        this.headers = PXCommonUtils.getHeadersFromRequest(request);

        if (headers.containsKey(Constants.MOBILE_SDK_AUTHORIZATION_HEADER) || headers.containsKey(Constants.MOBILE_SDK_TOKENS_HEADER)) {
            logger.debug(PXLogger.LogReason.DEBUG_MOBILE_SDK_DETECTED);
            this.isMobileToken = true;
            this.cookieOrigin = Constants.HEADER_ORIGIN;
        }
        parseCookies(request, isMobileToken);
        this.firstPartyRequest = false;
        this.userAgent = request.getHeader("user-agent");
        this.uri = request.getRequestURI();
        this.fullUrl = request.getRequestURL().toString();
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

        this.sensitiveRoute = checkSensitiveRoute(pxConfiguration.getSensitiveRoutes(), uri)
                || checkSensitiveRouteRegex(pxConfiguration.getSensitiveRoutesRegex(), uri);


        CustomParametersProvider customParametersProvider = pxConfiguration.getCustomParametersProvider();
        this.customParameters = customParametersProvider.buildCustomParameters(pxConfiguration, this);
    }

    private void parseCookies(HttpServletRequest request, boolean isMobileToken) {
        HeaderParser headerParser = new CookieHeaderParser();
        List<RawCookieData> tokens = new ArrayList<>();
        List<RawCookieData> originalTokens = new ArrayList<>();
        if (isMobileToken) {
            headerParser = new MobileCookieHeaderParser();

            String tokensHeader = request.getHeader(Constants.MOBILE_SDK_TOKENS_HEADER);
            tokens.addAll(headerParser.createRawCookieDataList(tokensHeader));

            String authCookieHeader = request.getHeader(Constants.MOBILE_SDK_AUTHORIZATION_HEADER);
            tokens.addAll(headerParser.createRawCookieDataList(authCookieHeader));

            String originalTokensHeader = request.getHeader(Constants.MOBILE_SDK_ORIGINAL_TOKENS_HEADER);
            originalTokens.addAll(headerParser.createRawCookieDataList(originalTokensHeader));

            String originalTokenHeader = request.getHeader(Constants.MOBILE_SDK_ORIGINAL_TOKEN_HEADER);
            originalTokens.addAll(headerParser.createRawCookieDataList(originalTokenHeader));

            this.tokens = tokens;
            if (!originalTokens.isEmpty()) {
                this.originalTokens = originalTokens;
            }

            ObjectMapper mapper = new ObjectMapper();
            this.pxde = mapper.createObjectNode();
            this.pxdeVerified = true;
        } else {
            Cookie[] cookies = request.getCookies();
            String cookieHeader = request.getHeader(Constants.COOKIE_HEADER_NAME);
            this.requestCookieNames = CookieNamesExtractor.extractCookieNames(cookies);
            setVidAndPxhd(cookies);
            tokens.addAll(headerParser.createRawCookieDataList(cookieHeader));
            this.tokens = tokens;
            DataEnrichmentCookie deCookie = headerParser.getRawDataEnrichmentCookie(this.tokens, this.pxConfiguration.getCookieKey());
            this.pxde = deCookie.getJsonPayload();
            this.pxdeVerified = deCookie.isValid();
        }
    }

    private void setVidAndPxhd(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("_pxvid") || cookie.getName().equals("pxvid")) {
                    this.vid = cookie.getValue();
                    this.vidSource = VidSource.VID_COOKIE;
                }
                if (cookie.getName().equals("_pxhd")) {
                    try {
                        this.pxhd = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Failed while decoding the pxhd value");
                    }
                }
            }
        }
    }

    public String getPxOriginalTokenCookie() {
        return originalTokenCookie;
    }

    public Boolean isBlocking() {
        return pxConfiguration.getModuleMode().equals(ModuleMode.BLOCKING);
    }

    public String getRiskMode() {
        return this.isBlocking() ? "active_blocking" : "monitor";
    }

    public void setOriginalTokenCookie(String originalTokenCookie) {
        this.originalTokenCookie = originalTokenCookie;
    }

    public void setRiskCookie(AbstractPXCookie riskCookie) {
        this.riskCookie = riskCookie.getDecodedCookie().toString();
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
            case Constants.BLOCK_ACTION_RATE:
                this.blockAction = BlockAction.RATE;
                break;
            default:
                this.blockAction = BlockAction.CAPTCHA;
                break;
        }
    }

    /**
     * Check if request is verified or not
     *
     * @return true if request is valid, false otherwise
     * @deprecated - Use {@link PXContext#isHandledResponse}
     */
    @Deprecated
    public boolean isVerified() {
        return verified;
    }

    /**
     * Check if request is verified or not, this method should not be used as a condition if to pass the request to
     * the application, instead use {@link PXContext#isHandledResponse} for the reason that its not
     * handling a case where we already responded if this is a first party request
     * <p>
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

    private boolean checkSensitiveRoute(Set<String> sensitiveRoutes, String uri) {
        for (String sensitiveRoutePrefix : sensitiveRoutes) {
            if (uri.startsWith(sensitiveRoutePrefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSensitiveRouteRegex(Set<String> sensitiveRoutes, String uri) {
        Pattern pattern;
        Matcher matcher;

        for (String sensitiveRouteRegex : sensitiveRoutes) {
            pattern = Pattern.compile(sensitiveRouteRegex);
            matcher = pattern.matcher(uri);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    public String getCollectorURL() {
        return String.format("%s%s%s", Constants.API_COLLECTOR_PREFIX, appId, Constants.API_COLLECTOR_POSTFIX);
    }

    public void setCookieVersion(String cookieVersion) {
        this.cookieVersion = cookieVersion;
    }

    public boolean isAdvancedBlockingResponse() {
        return pxConfiguration.isAdvancedBlockingResponse();
    }
}
