package com.perimeterx.api.blockhandler.templates;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.perimeterx.api.PXConfiguration;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by nitzangoldfeder on 02/03/2017.
 */
public abstract class TemplateFactory {

    public static String getTemplate(PXContext pxContext, PXConfiguration pxConfig, String template) throws PXException {
        try{
            Map<String,String> props = getProps(pxContext,pxConfig);

            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache m = mf.compile(getResourceFolder(template));
            StringWriter sw = new StringWriter();
            m.execute(sw, props).close();
            String html = sw.toString();

            return html;
        }catch(IOException e){
            throw new PXException(e);
        }

    }

    private static Map<String,String> getProps(PXContext pxContext, PXConfiguration pxConfig){
        Map<String,String> props = new HashMap<>();

        props.put("appId", pxConfig.getAppId());
        props.put("refId", pxContext.getUuid());
        props.put("vid", pxContext.getVid());
        props.put("uuid", pxContext.getUuid());
        props.put("cssRef", pxConfig.getCssRef());
        props.put("jsRef", pxConfig.getJsRef());
        props.put("logoVisibility", pxConfig.getCustomLogo() == null ? "hidden" : "visible");

        return props;
    }

    private static String getResourceFolder(String template){
        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = TemplateFactory.class.getClassLoader();
        File file = new File(classLoader.getResource(template).getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
