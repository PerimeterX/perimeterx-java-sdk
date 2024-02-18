package com.web;

import com.perimeterx.api.additionalContext.credentialsIntelligence.CIProtocol;
import com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse.LoginResponseValidationReportingMethod;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.CILoginMap;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.web.Utils.getEnforcerConfig;
import static com.web.Utils.jsonArrayToSet;

public class Config {
    private final JSONObject enforcerConfig;

    public Config() {
        enforcerConfig = getEnforcerConfig();
    }

    public PXConfiguration getPxConfiguration() {
        PXConfiguration.PXConfigurationBuilder builder = PXConfiguration.builder();
        for (String key : enforcerConfig.keySet()) {
            switch (key) {
                case "px_app_id":
                    builder.appId(enforcerConfig.getString(key));
                    break;
                case "px_cookie_secret":
                    builder.cookieKey(enforcerConfig.getString(key));
                    break;
                case "px_auth_token":
                    builder.authToken(enforcerConfig.getString(key));
                    break;
                case "px_logger_auth_token":
                    builder.loggerAuthToken(enforcerConfig.getString(key));
                    break;
                case "px_module_enabled":
                    builder.moduleEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_encryption_enabled":
                    builder.encryptionEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_blocking_score":
                    builder.blockingScore(enforcerConfig.getInt(key));
                    break;
                case "px_sensitive_headers":
                    builder.sensitiveHeaders(jsonArrayToSet(enforcerConfig.getJSONArray(key)));
                    break;
                case "px_max_buffer_len":
                    builder.maxBufferLen(enforcerConfig.getInt(key));
                    break;
                case "px_s2s_timeout":
                    builder.apiTimeout(enforcerConfig.getInt(key));
                    break;
                case "px_connection_timeout_ms":
                    builder.connectionTimeout(enforcerConfig.getInt(key));
                    break;
                case "px_send_async_activities":
                    builder.sendPageActivities(enforcerConfig.getBoolean(key));
                    break;
                case "px_backend_url":
                    builder.serverURL(enforcerConfig.getString(key));
                    break;
                case "px_custom_logo":
                    builder.customLogo(enforcerConfig.getString(key));
                    break;
                case "px_css_ref":
                    builder.cssRef(enforcerConfig.getString(key));
                    break;
                case "px_js_ref":
                    builder.jsRef(enforcerConfig.getString(key));
                    break;
                case "px_sensitive_routes":
                    builder.sensitiveRoutes(jsonArrayToSet(enforcerConfig.getJSONArray(key)));
                    break;
                case "px_sensitive_routes_regex":
                    builder.sensitiveRoutesRegex(jsonArrayToSet(enforcerConfig.getJSONArray(key)));
                    break;
                case "px_ip_headers":
                    builder.ipHeaders(jsonArrayToSet(enforcerConfig.getJSONArray(key)));
                    break;
                case "px_checksum":
                    builder.checksum(enforcerConfig.getString(key));
                    break;
                case "px_remote_configuration_enabled":
                    builder.remoteConfigurationEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_first_party_enabled":
                    builder.firstPartyEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_backend_collector_url":
                    builder.collectorUrl(enforcerConfig.getString(key));
                    break;
                case "px_module_mode":
                    if (enforcerConfig.getString(key).equals("active_blocking")) {
                        builder.moduleMode(ModuleMode.BLOCKING);
                    } else {
                        builder.moduleMode(ModuleMode.MONITOR);
                    }
                    break;
                case "px_filter_by_extension":
                    builder.staticFilesExt(jsonArrayToSet(enforcerConfig.getJSONArray(key)));
                    break;
                case "px_remote_configuration_interval_ms":
                    builder.remoteConfigurationInterval(enforcerConfig.getInt(key));
                    break;
                case "px_remote_configuration_delay_ms":
                    builder.remoteConfigurationDelay(enforcerConfig.getInt(key));
                    break;
                case "px_max_http_client_connections":
                    builder.maxConnections(enforcerConfig.getInt(key));
                    break;
                case "px_enforced_routes":
                    builder.enforcedRoutes(jsonArrayToSet(enforcerConfig.getJSONArray(key)));
                    break;
                case "px_monitored_routes":
                    builder.monitoredRoutes(jsonArrayToSet(enforcerConfig.getJSONArray(key)));
                    break;
                case "px_bypass_monitor_header":
                    builder.bypassMonitorHeader(enforcerConfig.getString(key));
                    break;
                case "px_login_credentials_extraction_enabled":
                    builder.loginCredentialsExtractionEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_login_credentials_extraction":
                    builder.loginCredentialsExtractionDetails(new CILoginMap(enforcerConfig.getJSONArray(key).toString()));
                    break;
                case "px_credentials_intelligence_version":
                    builder.ciProtocol(CIProtocol.getKeyByValue(enforcerConfig.getString(key)));
                    break;
                case "px_compromised_credentials_header":
                    builder.pxCompromisedCredentialsHeader(enforcerConfig.getString(key));
                    break;
                case "px_send_raw_username_on_additional_s2s_activity":
                    builder.addRawUsernameOnAdditionalS2SActivity(enforcerConfig.getBoolean(key));
                    break;
                case "px_additional_s2s_activity_header_enabled":
                    builder.additionalS2SActivityHeaderEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_login_successful_reporting_method":
                    builder.loginResponseValidationReportingMethod(LoginResponseValidationReportingMethod.getKeyByValue(enforcerConfig.getString(key)));
                    break;
                case "px_login_successful_body_regex":
                    builder.regexPatternToValidateLoginResponseBody(enforcerConfig.getString(key));
                    break;
                case "px_login_successful_header_name":
                    builder.headerNameToValidateLoginResponse(enforcerConfig.getString(key));
                    break;
                case "px_login_successful_header_value":
                    builder.headerValueToValidateLoginResponse(enforcerConfig.getString(key));
                    break;
                case "px_login_successful_status":
                    builder.loginResponseValidationStatusCode(extractStatusCode(key));
                    break;
                case "px_user_agent_max_length":
                case "px_risk_cookie_max_length":
                case "px_risk_cookie_max_iterations":
                case "px_risk_cookie_min_iterations":
                case "px_enable_login_creds_extraction":
                    //features are not supported yet
                    break;
            }

        }

        return builder.build();
    }

    private int[] extractStatusCode(String key) {
        final JSONArray jsonField = enforcerConfig.getJSONArray(key);
        final int[] statusCode = new int[jsonField.length()];

        for(int i = 0; i < statusCode.length; i ++) {
            statusCode[i] = jsonField.getInt(i);
        }
        return statusCode;
    }
}

