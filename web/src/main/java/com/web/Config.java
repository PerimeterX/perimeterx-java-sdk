package com.web;

import com.perimeterx.api.additionals2s.credentialsIntelligence.CIVersion;
import com.perimeterx.api.additionals2s.credentialsIntelligence.loginresponse.LoginResponseValidationReportingMethod;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import jdk.nashorn.api.scripting.AbstractJSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

import static com.web.Utils.*;

public class Config {
    private final JSONObject enforcerConfig;
    private final String SCRIPT_ENGINE_IMPLEMENTATION_NAME = "nashorn";

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
                case "px_enabled":
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
                case "px_max_buffer_length":
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
                case "px_server_url":
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
                case "px_collector_url":
                    builder.serverURL(enforcerConfig.getString(key));
                    break;
                case "px_module_mode":
                    if (enforcerConfig.getString(key).equals("active_blocking")) {
                        builder.moduleMode(ModuleMode.BLOCKING);
                    } else {
                        builder.moduleMode(ModuleMode.MONITOR);
                    }
                    break;
                case "px_static_files":
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
                    builder.loginCredentials(enforcerConfig.getJSONArray(key).toString());
                    break;
                case "px_credentials_intelligence_version":
                    builder.ciVersion(CIVersion.getKeyByValue(enforcerConfig.getString(key)));
                    break;
                case "px_compromised_credentials_header":
                    builder.pxCompromisedCredentialsHeader(enforcerConfig.getString(key));
                    break;
                case "px_send_raw_username_on_additional_s2s_activity":
                    builder.isAllowToAddRawUserNameOnS2SActivity(enforcerConfig.getBoolean(key));
                    break;
                case "px_additional_s2s_activity_header_enabled":
                    builder.additionalS2SActivityHeaderEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_login_successful_reporting_method":
                    builder.loginResponseValidationReportingMethod(LoginResponseValidationReportingMethod.getKeyByValue(enforcerConfig.getString(key)));
                    break;
                case "px_login_successful_body_regex":
                    builder.loginResponseValidationRegexBody(enforcerConfig.getString(key));
                    break;
                case "px_login_successful_header_name":
                    builder.loginResponseValidationHeaderName(enforcerConfig.getString(key));
                    break;
                case "px_login_successful_header_value":
                    builder.loginResponseValidationHeaderValue(enforcerConfig.getString(key));
                    break;
                case "px_login_successful_status":
                    builder.loginResponseValidationStatusCode(unpackStatusCode(key));
                    break;
                case "px_login_successful_custom_callback":
                    builder.loginResponseValidationCustomCallback(extractCustomCallback(key));
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

    private int[] unpackStatusCode(String key) {
        final JSONArray jsonField = enforcerConfig.getJSONArray(key);
        final int[] statusCode = new int[jsonField.length()];

        for(int i = 0; i < statusCode.length; i ++) {
            statusCode[i] = jsonField.getInt(i);
        }
        return statusCode;
    }

    private Function<HttpServletResponse, Boolean> extractCustomCallback(String key) {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName(SCRIPT_ENGINE_IMPLEMENTATION_NAME);
        try {
            final AbstractJSObject callback = (AbstractJSObject) engine.eval(enforcerConfig.getString(key));
            return re -> (Boolean) callback.call(null, re);
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }
}

