package com.web;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.web.Utils.setDefaultPageAttributes;

@WebFilter("/*")
public class PXFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(PXFilter.class);

    private PerimeterX pxFilter;
    private Config config;

    public void init(FilterConfig filterConfig) {
        try {
            config = new Config();
            pxFilter = new PerimeterX(config.getPxConfiguration());
            log.info(
                    "PerimeterX enforcer initialized; java.version={} java.specification.version={} java.vm.name={} java.vm.version={}",
                    System.getProperty("java.version"),
                    System.getProperty("java.specification.version"),
                    System.getProperty("java.vm.name"),
                    System.getProperty("java.vm.version"));

        } catch (PXException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
             request = new RequestWrapper((HttpServletRequest) request);

            final PXContext context = pxFilter.pxVerify((HttpServletRequest) request, new HttpServletResponseWrapper((HttpServletResponse) response));

            setDefaultPageAttributes((HttpServletRequest) request, config);
            copyDataEnrichmentHeaderToResponse((HttpServletRequest) request, (HttpServletResponse) response);

            if (context == null || !context.isHandledResponse()) {
                filterChain.doFilter(request, response);
            }

            response = new ResponseWrapper((HttpServletResponse) response);
            pxFilter.pxPostVerify((ResponseWrapper) response, context);

        } catch (PXException e) {
            filterChain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
        try {
            pxFilter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyDataEnrichmentHeaderToResponse(HttpServletRequest request, HttpServletResponse response) {
        String dataEnrichmentHeaderName = config.getPxConfiguration().getPxDataEnrichmentHeaderName();
        if (dataEnrichmentHeaderName == null || dataEnrichmentHeaderName.isEmpty()) {
            return;
        }

        String dataEnrichmentHeaderValue = request.getHeader(dataEnrichmentHeaderName);
        if (dataEnrichmentHeaderValue != null && !dataEnrichmentHeaderValue.isEmpty()) {
            response.setHeader(dataEnrichmentHeaderName, dataEnrichmentHeaderValue);
        }
    }
}
