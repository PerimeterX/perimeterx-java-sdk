package com.perimeterx.api.additionalContext.credentialsIntelligence.loginresponse;

import com.perimeterx.http.ResponseWrapper;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.logger.IPXLogger;

public class LoginResponseBodyValidator implements LoginResponseValidator{
    private final String configRegexBody;
    private final IPXLogger logger;

    public LoginResponseBodyValidator(PXConfiguration config, PXContext context) {
        this.configRegexBody = config.getRegexPatternToValidateLoginResponseBody();
        this.logger = context.logger;
    }

    @Override
    public boolean isSuccessfulLogin(ResponseWrapper response) {
        if(response == null || response.toString() == null) {
            return false;
        }

        return response.toString().matches(this.configRegexBody);
    }
}
