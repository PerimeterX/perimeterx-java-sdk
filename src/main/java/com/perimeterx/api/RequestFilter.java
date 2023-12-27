package com.perimeterx.api;

import com.perimeterx.http.PXHttpMethod;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.logger.IPXLogger;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestFilter {
    private static final IPXLogger logger = PerimeterX.globalLogger;
    private final PXConfiguration configuration;

    public RequestFilter(PXConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isFilteredRequest(HttpServletRequest req, PXContext context) {
        return isExtensionWhiteListed(req.getServletPath(), req.getMethod(), context)
                || isFilteredByCustomFunction(req,context);
    }

    protected boolean isExtensionWhiteListed(String path, String method, PXContext context) {
        if (!method.equalsIgnoreCase(PXHttpMethod.GET.name())) {
            return false;
        }

        boolean isStaticFileWhiteListed = configuration.getStaticFilesExt().contains(FilenameUtils.getExtension(path));
        if (isStaticFileWhiteListed){
            context.logger.debug("isExtensionWhiteListed - filter request");
        }
        return  isStaticFileWhiteListed;
    }

    protected boolean isFilteredByCustomFunction(HttpServletRequest req, PXContext context) {
        try {
            final boolean test = configuration.getFilterByCustomFunction().test(req);
            if (test) {
                context.logger.debug("isFilteredByCustomFunction - filter request");
            }

            return test;
        } catch (Exception e) {
            context.logger.error("isFilteredByCustomFunction - exception during filter", e.getMessage());
        }
        return false;
    }
}
