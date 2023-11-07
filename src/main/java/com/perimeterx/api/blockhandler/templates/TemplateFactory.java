package com.perimeterx.api.blockhandler.templates;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a helper class, in order to get a template the factory receives a name of a template and returns
 * the template compiled
 * <p>
 * Created by nitzangoldfeder on 02/03/2017.
 */
public abstract class TemplateFactory {

    public static String getTemplate(String template, Map<String, String> props) throws PXException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache m = mf.compile((getTemplate(template)), (getTemplate(template)).toString());
            StringWriter sw = new StringWriter();
            m.execute(sw, props).close();
            return sw.toString();
        } catch (IOException e) {
            throw new PXException(e);
        }

    }

    public static Map<String, String> getProps(PXContext pxContext, PXConfiguration pxConfig) {
        Map<String, String> props = new HashMap<>();

        props.put("appId", pxConfig.getAppId());
        props.put("vid", pxContext.getVid());
        props.put("uuid", pxContext.getUuid());
        props.put("customLogo", pxConfig.getCustomLogo());
        props.put("cssRef", pxConfig.getCssRef());
        props.put("jsRef", pxConfig.getJsRef());
        props.put("isMobile", pxContext.isMobileToken() ? "true" : "false");

        String captchaSrcParams = getCaptchaSrcParams(pxContext);
        String blockScript = getCaptchaUrl(Constants.CAPTCHA_HOST, pxConfig.getAppId(), captchaSrcParams);
        String altBlockScript = getCaptchaUrl(Constants.ALT_CAPTCHA_HOST, pxConfig.getAppId(), captchaSrcParams);


        String jsClientSrc = "//" + Constants.CLIENT_HOST + "/" + pxConfig.getAppId() + "/main.min.js";
        String hostUrl = pxContext.getCollectorURL();
        if (pxConfig.isFirstPartyEnabled() && !pxContext.isMobileToken()) {
            String prefix = pxConfig.getAppId().substring(2);
            blockScript = "/" + prefix + Constants.FIRST_PARTY_CAPTCHA_PATH + "/captcha.js?" + captchaSrcParams;
            jsClientSrc = "/" + prefix + Constants.FIRST_PARTY_VENDOR_PATH;
            hostUrl = "/" + prefix + Constants.FIRST_PARTY_XHR_PATH;
        }
        props.put("hostUrl", hostUrl);
        props.put("blockScript", blockScript);
        props.put("altBlockScript", altBlockScript);
        props.put("jsClientSrc", jsClientSrc);
        props.put("firstPartyEnabled", pxConfig.isFirstPartyEnabled() ? "true" : "false");
        props.put("blockedUrl", pxContext.getFullUrl());

        return props;
    }

    private static StringReader getTemplate(String template) throws IOException {
        String templateString;
        InputStream templateStream = TemplateFactory.class.getResourceAsStream(template);
        if (templateStream == null) {
            throw new RuntimeException("Unable to find " + template + " make sure its located in the resources dir");
        }
        templateString = IOUtils.toString(templateStream);
        return new StringReader(templateString);
    }


    private static String getCaptchaSrcParams(PXContext pxContext) {
        String urlVid = pxContext.getVid() != null ? pxContext.getVid() : "";
        return "a=" + pxContext.getBlockAction().getCode() + "&u=" + pxContext.getUuid() + "&v=" + urlVid + "&m=" + (pxContext.isMobileToken() ? "1" : "0");
    }

    private static String getCaptchaUrl(String host, String appId, String params) {
        return "//" + host + "/" + appId + "/captcha.js?" + params;
    }
}