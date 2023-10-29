package com.perimeterx.api;

import com.perimeterx.http.PXHttpMethod;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.PXLogger;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestFilter {
    private static final PXLogger logger = PXLogger.getLogger(PerimeterX.class);
    private final PXConfiguration configuration;

    public RequestFilter(PXConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isFilteredRequest(HttpServletRequest req) {
        return !moduleEnabled() ||
                isExtensionWhiteListed(req.getServletPath(), req.getMethod())
                || isFilteredByCustomFunction(req);
    }

    protected boolean moduleEnabled() {
        final boolean isModuleEnabled = this.configuration.isModuleEnabled();

        if (isModuleEnabled) {
            logger.debug(PXLogger.LogReason.DEBUG_MODULE_DISABLED);
        }
        return this.configuration.isModuleEnabled();
    }

    protected boolean isExtensionWhiteListed(String path, String method) {
        if (!method.equalsIgnoreCase(PXHttpMethod.GET.name())) {
            return false;
        }

        return configuration.getStaticFilesExt()
                .contains(FilenameUtils.getExtension(path));
    }

    protected boolean isFilteredByCustomFunction(HttpServletRequest req) {
        try {
            final boolean test = configuration.getFilterByCustomFunction().test(req);
            if (test) {
                logger.debug("isFilteredByCustomFunction - filter request");
            }

            return test;
        } catch (Exception e) {
            logger.error("isFilteredByCustomFunction - exception during filter");
        }
        return false;
    }
}
