package com.perimeterx.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.api.additionalContext.LoginData;
import com.perimeterx.api.additionalContext.PXHDSource;
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
import com.perimeterx.models.enforcererror.EnforcerErrorReasonInfo;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.risk.*;
import com.perimeterx.utils.*;
import com.perimeterx.utils.logger.IPXLogger;
import com.perimeterx.utils.logger.LogReason;
import com.perimeterx.utils.logger.LoggerFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.perimeterx.utils.Constants.BREACHED_ACCOUNT_KEY_NAME;
import static com.perimeterx.utils.Constants.LOGGER_TOKEN_HEADER_NAME;
import static com.perimeterx.utils.PXCommonUtils.cookieHeadersNames;
import static com.perimeterx.utils.PXCommonUtils.cookieKeysToCheck;

/**
 * PXContext - Populate relevant data from HttpRequest
 * <p>
 * Created by shikloshi on 03/07/2016.
 */
@Data
public class PXContext {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public IPXLogger logger;

    /**
     * Original HTTP request
     */
    private HttpServletRequest request;

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
    private String userAgent;
    private String fullUrl;
    private String httpMethod;
    private String httpVersion;

    // PerimeterX computed data on the request
    private String riskCookie;
    private String appId;

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
     * Reason for request being verified
     *
     * @see com.perimeterx.models.risk.PassReason
     */
    private PassReason passReason;

    /**
     * Reason for s2s_error if occurred
     *
     * @see com.perimeterx.models.risk.S2SErrorReasonInfo
     */
    private S2SErrorReasonInfo s2sErrorReasonInfo;

    /**
     * Reason for enforcer_error if occurred
     *
     * @see com.perimeterx.models.risk.S2SErrorReasonInfo
     */
    private EnforcerErrorReasonInfo enforcerErrorReasonInfo;

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
    private PXHDSource pxhdSource;
    private boolean isMonitoredRequest;
    private LoginData loginData;
    private UUID requestId;
    private Set<String> sensitiveHeaders;
    private String additionalRiskInfo;
    private String servletPath;
    private String pxhdDomain;
    private String pxCtsCookie;
    private long enforcerStartTime;

    /**
     * The cookie key used to decrypt the cookie
     */
    private String cookieKeyUsed;

    /**
     * The base64 encoded request full url
     */
    private String encodedBlockedUrl;

    /**
     * The base64 encoded request http method
     */
    private String encodedHttpMethod;

    public PXContext(final HttpServletRequest request, final IPProvider ipProvider, final HostnameProvider hostnameProvider, PXConfiguration pxConfiguration) {
        this.pxConfiguration = pxConfiguration;
        this.enforcerStartTime = new Date().getTime();
        this.request = request;
        this.headers = PXCommonUtils.getHeadersFromRequest(request);
        this.logger = getLogger();
        logger.debug(LogReason.DEBUG_REQUEST_CONTEXT_CREATED);
        this.appId = pxConfiguration.getAppId();
        this.ip = ipProvider.getRequestIP(request);
        this.hostname = hostnameProvider.getHostname(request);
        postInitContext(request, pxConfiguration);
    }

    //This constructor is in use by unit tests where we don't need context but only logger
    public PXContext(LoggerFactory loggerFactory) {
        this.logger = loggerFactory.getRequestContextLogger();
    }

    private void postInitContext(final HttpServletRequest request, PXConfiguration pxConfiguration) {
        if (headers.containsKey(Constants.MOBILE_SDK_AUTHORIZATION_HEADER) || headers.containsKey(Constants.MOBILE_SDK_TOKENS_HEADER)) {
            logger.debug(LogReason.DEBUG_MOBILE_SDK_DETECTED);
            this.isMobileToken = true;
            this.cookieOrigin = Constants.HEADER_ORIGIN;
        }
        parseCookies(request, isMobileToken);
        generateLoginData(request, pxConfiguration);
        this.firstPartyRequest = false;
        this.userAgent = request.getHeader("user-agent");
        this.servletPath = request.getServletPath();
        this.fullUrl = extractURL(request); //full URL with query string
        this.blockReason = BlockReason.NONE;
        this.passReason = PassReason.NONE;
        this.s2sErrorReasonInfo = new S2SErrorReasonInfo();
        this.madeS2SApiCall = false;
        this.riskRtt = 0;
        this.httpMethod = request.getMethod();
        this.isMonitoredRequest = !shouldBypassMonitor() && shouldMonitorRequest();
        this.requestId = UUID.randomUUID();
        this.enforcerErrorReasonInfo = new EnforcerErrorReasonInfo();
        this.sensitiveHeaders = pxConfiguration.getSensitiveHeaders();

        String protocolDetails[] = request.getProtocol().split("/");
        this.httpVersion = protocolDetails.length > 1 ? protocolDetails[1] : StringUtils.EMPTY;

        CustomParametersProvider customParametersProvider = pxConfiguration.getCustomParametersProvider();
        Function<? super HttpServletRequest, ? extends CustomParameters> customParametersExtraction = pxConfiguration.getCustomParametersExtraction();
        try {
            if (customParametersExtraction != null) {
                this.customParameters = customParametersExtraction.apply(this.request);
            } else {
                this.customParameters = customParametersProvider.buildCustomParameters(pxConfiguration, this);
            }
        } catch (Exception e) {
            logger.debug("failed to extract custom parameters from custom function", e);
        }
    }

    private IPXLogger getLogger(){
        String requestLoggerAuthToken = this.getHeaders().get(LOGGER_TOKEN_HEADER_NAME);
        boolean isLoggerHeaderRequest = requestLoggerAuthToken!=null && this.getPxConfiguration().getLoggerAuthToken().equals(requestLoggerAuthToken);
        return pxConfiguration.getLoggerFactory().getRequestContextLogger(isLoggerHeaderRequest);
    }
    public boolean isSensitiveRequest() {
        return this.isContainCredentialsIntelligence()
                || checkSensitiveRoute(pxConfiguration.getSensitiveRoutes(), servletPath)
                || checkSensitiveRouteRegex(pxConfiguration.getSensitiveRoutesRegex(), servletPath)
                || isCustomSensitive();
    }

    private boolean isCustomSensitive() {
        try {
            final boolean test = this.pxConfiguration.getCustomIsSensitiveRequest().test(this.request);
            if (test) {
                logger.debug("custom sensitive request");
            }
            return test;
        } catch (Exception e) {
            logger.debug("exception in custom sensitive function", e);
        }
        return false;
    }

    private boolean isRoutesContainUri(Set<String> routes, String uri) {
        Pattern pattern;
        Matcher matcher;

        for (String routeExpression : routes) {
            pattern = Pattern.compile(routeExpression, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(uri);

            if (matcher.matches()) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldMonitorRequest() {
        return ((pxConfiguration.getModuleMode().equals(ModuleMode.MONITOR) &&
                !isRoutesContainUri(this.pxConfiguration.getEnforcedRoutes(), servletPath)) ||
                isRoutesContainUri(this.pxConfiguration.getMonitoredRoutes(), servletPath));
    }

    private boolean shouldBypassMonitor() {
        String bypassHeader = this.pxConfiguration.getBypassMonitorHeader();
        return !StringUtils.isEmpty(bypassHeader) && this.getHeaders().containsKey(bypassHeader.toLowerCase())
                && this.getHeaders().get(bypassHeader.toLowerCase()).equals("1");
    }

    private String extractURL(ServletRequest request) {
        StringBuffer requestURL = ((HttpServletRequest) request).getRequestURL();
        if (((HttpServletRequest) request).getQueryString() != null) {
            requestURL.append("?").append(((HttpServletRequest) request).getQueryString());
        }
        return requestURL.toString();
    }

    private void parseCookies(HttpServletRequest request, boolean isMobileToken) {
        HeaderParser headerParser = new CookieHeaderParser(logger);
        List<RawCookieData> tokens = new ArrayList<>();
        List<RawCookieData> originalTokens = new ArrayList<>();
        if (isMobileToken) {
            headerParser = new MobileCookieHeaderParser(logger);

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
            String[] cookieHeaders = cookieHeadersNames(getPxConfiguration())
                    .stream()
                    .map(request::getHeader)
                    .toArray(String[]::new);
            this.requestCookieNames = CookieNamesExtractor.extractCookieNames(cookies);
            setVidPxhdAndPxcts(cookies);
            tokens.addAll(headerParser.createRawCookieDataList(cookieHeaders));
            this.tokens = tokens;
            DataEnrichmentCookie deCookie = headerParser.getRawDataEnrichmentCookie(this.tokens, cookieKeysToCheck(this, this.pxConfiguration));
            this.pxde = deCookie.getJsonPayload();
            this.pxdeVerified = deCookie.isValid();
        }
    }

    private void setVidPxhdAndPxcts(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("_pxvid") || cookie.getName().equals("pxvid")) {
                    if (isValidVid(cookie.getValue())) {
                        this.vid = cookie.getValue();
                        this.vidSource = VidSource.VID_COOKIE;
                    } else {
                        logger.debug("setVidAndPxhd - invalid VID value was extracted");
                    }
                }
                if (cookie.getName().equals("_pxhd")) {
                    try {
                        this.pxhd = URLDecoder.decode(cookie.getValue(), "UTF-8");
                        this.setPxhdSource(PXHDSource.COOKIE);
                    } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                        logger.error("setVidAndPxhd - failed while decoding the pxhd value", e);
                    }
                }
                if (cookie.getName().equals("pxcts")) {
                    this.pxCtsCookie = cookie.getValue();
                }
            }
        }
    }

    public String getPxOriginalTokenCookie() {
        return originalTokenCookie;
    }

    public Boolean isMonitoredRequest() {
        return this.isMonitoredRequest;
    }

    public String getRiskMode() {
        return this.isMonitoredRequest() ? "monitor" : "active_blocking";
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

    private void generateLoginData(HttpServletRequest request, PXConfiguration pxConfiguration) {
        final LoginData loginData;

        try {
            loginData = new LoginData(request, pxConfiguration, this.logger);
            this.setLoginData(loginData);
        } catch (PXException pxe) {
            logger.error("Failed to generate login data. Error :: ", pxe);
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
        return pxConfiguration.getCollectorUrl();
    }

    public void setCookieVersion(String cookieVersion) {
        this.cookieVersion = cookieVersion;
    }

    public boolean isAdvancedBlockingResponse() {
        return pxConfiguration.isAdvancedBlockingResponse();
    }

    public boolean isBreachedAccount() {
        return this.pxde != null && this.pxdeVerified && this.pxde.has(BREACHED_ACCOUNT_KEY_NAME);
    }

    public boolean isContainCredentialsIntelligence() {
        return this.getLoginData() != null && this.getLoginData().getLoginCredentials() != null;
    }

    private boolean isValidVid(String vidCookieValue) {
        Pattern vidPattern = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = vidPattern.matcher(vidCookieValue);
        return matcher.matches();
    }
}
