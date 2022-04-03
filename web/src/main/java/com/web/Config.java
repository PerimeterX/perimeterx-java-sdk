package com.web;

import com.perimeterx.api.additionals2s.credentialsIntelligence.CIVersion;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import org.json.JSONObject;
import static com.web.Utils.*;

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
                case "px_login_credentials_extraction_enabled":
                    builder.loginCredentialsExtractionEnabled(enforcerConfig.getBoolean(key));
                    break;
                case "px_login_credentials_extraction":
                    builder.loginCredentials(enforcerConfig.getJSONArray(key).toString());
                    break;
                case "px_credentials_intelligence_version":
                    builder.ciVersion(CIVersion.getKeyByValue(enforcerConfig.getString(key)));
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
}

