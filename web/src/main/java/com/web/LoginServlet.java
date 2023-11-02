package com.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.http.RequestWrapper;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.ConfigCredentialsFieldPath;
import com.perimeterx.models.configuration.credentialsIntelligenceconfig.LoginCredentials;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static com.perimeterx.utils.Constants.DEFAULT_COMPROMISED_CREDENTIALS_HEADER_NAME;
import static com.perimeterx.utils.StringUtils.extractCredentialsFromMultipart;
import static com.perimeterx.utils.StringUtils.splitQueryParams;

@WebServlet({"/login", "/login-nested-object"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleLoginRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleLoginRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleLoginRequest(request, response);
    }

    public void handleLoginRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LoginCredentials creds = extractCredentials(request);

        if(isValidCredentials(creds)){
            if (Objects.equals(request.getHeader(DEFAULT_COMPROMISED_CREDENTIALS_HEADER_NAME), "1")) {
                response.addHeader(DEFAULT_COMPROMISED_CREDENTIALS_HEADER_NAME, "1");
            }
            request.getRequestDispatcher("/templates/profile.template.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

    private boolean isValidCredentials(LoginCredentials creds) {
        return creds != null && creds.getUsername().equals(Constants.PX_USERNAME) && creds.getPassword().equals(Constants.PX_PASSWORD);
    }

    private LoginCredentials extractCredentials(HttpServletRequest request) {
        try {
            final String body = ((RequestWrapper) request).getBody();
            final boolean isFormUrlEncoded = body.contains("=") && body.contains("&");
            final String contentType = request.getContentType();
            Map<String, String> params;

            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                return extractCredentialsFromMultipart(body, new ConfigCredentialsFieldPath("username", "password"));
            } else if (isFormUrlEncoded) {
                params = splitQueryParams(body);
                return new LoginCredentials(params.get("username"), params.get("password"));
            } else {
                params = extractJsonFields(body);
                return new LoginCredentials(params.get("username"), params.get("password"));
            }
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> extractJsonFields(String body) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(body);

        Map<String, String> fieldMap = new HashMap<>();

        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue().asText();
                fieldMap.put(fieldName, fieldValue);
            }
        }

        return fieldMap;
    }
}
