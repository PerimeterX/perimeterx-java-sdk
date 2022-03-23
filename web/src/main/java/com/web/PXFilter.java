package com.web;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

@WebFilter("/*")
public class PXFilter implements Filter {
    private PerimeterX pxFilter;

    public void init(FilterConfig filterConfig) {
        try {
            Config config = new Config();
            pxFilter = new PerimeterX(config.getPxConfiguration());

        } catch (PXException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {

            PXContext context = pxFilter.pxVerify((HttpServletRequest) request, new HttpServletResponseWrapper((HttpServletResponse) response));

            if (context != null && context.isRequestLowScore()) {
                filterChain.doFilter(request, response);
            }


        } catch (PXException e) {
            filterChain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

    }
}
