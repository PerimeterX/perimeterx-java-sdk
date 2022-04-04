package com.web;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static com.web.Utils.setDefaultPageAttributes;

@WebFilter("/*")
public class PXFilter implements Filter {
    private PerimeterX pxFilter;
    private Config config;

    public void init(FilterConfig filterConfig) {
        try {
            config = new Config();
            pxFilter = new PerimeterX(config.getPxConfiguration());

        } catch (PXException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            request = new RequestWrapper((HttpServletRequest) request);
            response = new ResponseWrapper((HttpServletResponse) response);
            PXContext context = pxFilter.pxVerify((HttpServletRequest) request, new HttpServletResponseWrapper((HttpServletResponse) response));
            setDefaultPageAttributes((HttpServletRequest) request, config);

            if (context != null && context.isRequestLowScore()) {
                filterChain.doFilter(request, response);
            }

            pxFilter.pxPostVerify((ResponseWrapper) response, context);

        } catch (PXException e) {
            filterChain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

    }
}
