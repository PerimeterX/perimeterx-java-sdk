package com.perimeterx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PXLogger {

    private Logger logger;

    private final String DEBUG_PREFIX = "[PerimeterX - DEBUG] ";
    private final String ERROR_PREFIX = "[PerimeterX - ERROR] ";

    public enum LogReasson {

        INFO_MODULE_DISABLED("Request will not be verified, module is disabled"),
        INFO_STARTING_REQUEST_VERIFICTION("Starting request verification"),
        INFO_REQUEST_CONTEXT_CREATED("Request context created successfully"),

        INFO_NO_CAPTCHA_COOKIE("No Captcha cookie present on the request"),
        INFO_CAPTCHA_COOKIE_FOUND("Captcha cookie found, evaluating"),

        INFO_COOKIE_MISSING("Cookie is missing"),
        INFO_COOKIE_VERSION_FOUND("Cookie {} found, Evaluating"), //version
        INFO_COOKIE_DECRYPTION_FAILED("Cookie decryption failed, value: {}"), //cookie value
        INFO_COOKIE_DECRYPTION_HMAC_FAILED("Cookie HMAC validation failed, value: {}, user-agent: {}"), //decrypted-cookie-value, user agent
        INFO_COOKIE_TLL_EXPIRED("Cookie TTL is expired, value: {}, age: {}"), //decrypted-cookie-value, cookie age
        INFO_COOKIE_EVALUATION_FINISHED("Cookie evaluation ended successfully, risk score: {}"), //score

        INFO_S2S_RISK_API_SENSITIVE_ROUTE("Sensitive route match, sending Risk API. path: {}"), //path
        INFO_S2S_RISK_API_REQUEST("Evaluating Risk API request, call reason: {}"), //s2s_call_reason
        INFO_S2S_RISK_API_RESPONSE("Risk API response returned successfully, risk score: {}, round_trip_time: {}"), //score,rtt

        INFO_S2S_SCORE_IS_HIGHER_THAN_BLOCK("Risk score is higher than blocking score. score: {} blockingScore: {}"),
        INFO_S2S_SCORE_IS_LOWER_THAN_BLOCK("Risk score is lower than blocking score. score: {} blockingScore: {}"),
        INFO_S2S_ENFORCING_ACTION("Enforcing action: {} page is served"), //Block/Captcha/Challenge

        ERROR_CONFIGURATION_MISSING_MANDATORY_CONFIGURATION("Unable to initialize module, missing mandatory configuration. {}"), //config name
        ERROR_CONFIGURATION_INVALID_CONFIGURATION("Unable to initialize module, invalid configuration. {}: {}"),//config-name, config value
        ERROR_CONFIGURATION_PARSING_EXCEPTION("Unexpected exception while parsing configurations. {}"), //error
        ERROR_CAPTCHA_EVALUATION_EXCEPTION("Unexpected exception while evaluating Captcha cookie. {}"),//error
        ERROR_COOKIE_EVALUATION_EXCEPTION("Unexpected exception while evaluating Risk cookie. {}");//error

        String reason;

        LogReasson(String reason) {
            this.reason = reason;
        }

        public String toString() {
            return this.reason;
        }
    }

    public static PXLogger getLogger(Class<?> clazz) {
        return new PXLogger(clazz);
    }

    private PXLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }


    public void info(LogReasson reasson, Object... args) {
        logger.info(DEBUG_PREFIX + reasson, args);
    }

    public void info(String msg, Object... args) {
        logger.info(DEBUG_PREFIX + msg, args);
    }

    public void error(LogReasson reasson, Object... args) {
        logger.error(ERROR_PREFIX + reasson, args);
    }

    public void error(String msg, Object... args) {
        logger.error(ERROR_PREFIX + msg, args);
    }
}