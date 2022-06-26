package com.perimeterx.models.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perimeterx.api.additionalContext.LoginData;
import com.perimeterx.models.PXContext;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalS2SActivityDetails extends CommonActivityDetails {

    @JsonProperty("client_uuid")
    private String clientUuid;

    @JsonProperty("raw_username")
    public String username;

    @JsonProperty("login_successful")
    public Boolean loginSuccessful;

    @JsonProperty("http_status_code")
    public Integer httpStatusCode;


    public AdditionalS2SActivityDetails(PXContext context) {
        super(context);

        final LoginData loginData = context.getLoginData();
        this.clientUuid = context.getUuid();
        this.username = null;
        this.loginSuccessful = loginData.getLoginSuccessful();
        this.httpStatusCode = loginData.getResponseStatusCode();
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
