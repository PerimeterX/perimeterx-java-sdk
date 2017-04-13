package com.perimeterx.internals.cookie;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public abstract class PXCookieFactory {
    private static final String PATH = "com.perimeterx.internals.cookie.";


    public static PXCookie create(PXConfiguration pxConfiguration, PXContext pxContext) throws PXException {
        // Return null if no cookies
        Set<String> cookieKeys = pxContext.getPxCookies().keySet();
        if (cookieKeys.isEmpty()){
            return null;
        }

        // Try to load a class, will get the higher cookie because keys are sorted
        String cookieType = cookieKeys.iterator().next();
        try {
            Constructor cookieConstructor = Class.forName(PATH.concat(cookieType)).getConstructor(PXConfiguration.class, PXContext.class);
            return (PXCookie) cookieConstructor.newInstance(pxConfiguration, pxContext);
        } catch (NoSuchMethodException | ClassNotFoundException| IllegalAccessException| InstantiationException | InvocationTargetException e) {
           throw new PXException("Unable to create Cookie from context".concat( e.getMessage()));
        }
    }
}
