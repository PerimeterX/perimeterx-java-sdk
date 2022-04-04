package com.web;

import com.perimeterx.http.RequestWrapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static com.perimeterx.utils.StringUtils.splitQueryParams;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Map<String, String> queryParams = splitQueryParams(((RequestWrapper) request).getBody());
        final String username = queryParams.get("username");
        final String password = queryParams.get("password");

        if(username.equals(Constants.PX_USERNAME) && password.equals(Constants.PX_PASSWORD)){
            response.sendRedirect(request.getContextPath() + "/profile");
        } else {
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

}
