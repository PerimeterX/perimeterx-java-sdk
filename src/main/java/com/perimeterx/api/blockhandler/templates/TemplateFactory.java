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

import static com.perimeterx.utils.Constants.*;
import static com.perimeterx.utils.FirstPartyUtil.getFirstPartyCaptchaURL;

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

        String captchaSrcParams = getCaptchaSrcParams(pxContext);
        final String altBlockScript = getFirstPartyCaptchaURL(pxConfig, captchaSrcParams, true);
        String blockScript = getFirstPartyCaptchaURL(pxConfig, captchaSrcParams, false);


        String jsClientSrc = URL_HTTPS_PREFIX + Constants.CLIENT_HOST + SLASH + pxConfig.getAppId() + SENSOR_FIRST_PARTY_PATH;
        String hostUrl = pxContext.getCollectorURL();
        if (pxConfig.isFirstPartyEnabled() && !pxContext.isMobileToken()) {
            String prefix = pxConfig.getAppId().substring(2);
            blockScript = SLASH + prefix + Constants.FIRST_PARTY_CAPTCHA_PATH + QUESTION_MARK + captchaSrcParams;
            jsClientSrc = SLASH + prefix + Constants.FIRST_PARTY_VENDOR_PATH;
            hostUrl = SLASH + prefix + Constants.FIRST_PARTY_XHR_PATH;
        }
        props.put("hostUrl", hostUrl);
        props.put("blockScript", blockScript);
        props.put("altBlockScript", altBlockScript);
        props.put("jsClientSrc", jsClientSrc);
        props.put("firstPartyEnabled", pxConfig.isFirstPartyEnabled() ? "true" : "false");
        props.put("blockedUrl", pxContext.getFullUrl());
        props.put("isMobile", Boolean.toString(pxContext.isMobileToken()));

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
}