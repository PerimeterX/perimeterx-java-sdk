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

    public static String getTemplate(PXContext pxContext, PXConfiguration pxConfig, String template) throws PXException {
        try {
            // In case of challenge
            if (pxContext.getBlockAction().equals("challenge") && pxContext.getBlockActionData() != null) {
                return pxContext.getBlockActionData();
            }

            Map<String, String> props = getProps(pxContext, pxConfig);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache m = mf.compile((getTemplate(template)), (getTemplate(template)).toString());
            StringWriter sw = new StringWriter();
            m.execute(sw, props).close();
            return sw.toString();
        } catch (IOException e) {
            throw new PXException(e);
        }

    }

    private static Map<String, String> getProps(PXContext pxContext, PXConfiguration pxConfig) {
        Map<String, String> props = new HashMap<>();

        props.put("appId", pxConfig.getAppId());
        props.put("refId", pxContext.getUuid());
        props.put("vid", pxContext.getVid());
        props.put("uuid", pxContext.getUuid());
        props.put("customLogo", pxConfig.getCustomLogo());
        props.put("cssRef", pxConfig.getCssRef());
        props.put("jsRef", pxConfig.getJsRef());
        //captcha.mobile.mustache prop
        props.put("hostUrl", pxContext.getCollectorURL());
        props.put("jsClientSrc", pxConfig.isFirstPartyEnabled() ? String.format("/%s/init.js", pxConfig.getAppId().substring(2)) : String.format("%s/%s/main.min.js", Constants.CLIENT_HOST, pxConfig.getAppId()));
        props.put("firstPartyEnabled", pxConfig.isFirstPartyEnabled() ? "true" : null);
        props.put("logoVisibility", pxConfig.getCustomLogo() == null ? "hidden" : "visible");

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
}