package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nitzangoldfeder on 03/04/2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomParameters {
    @JsonProperty("custom_param1")
    public String customParam1;
    @JsonProperty("custom_param2")
    public String customParam2;
    @JsonProperty("custom_param3")
    public String customParam3;
    @JsonProperty("custom_param4")
    public String customParam4;
    @JsonProperty("custom_param5")
    public String customParam5;
    @JsonProperty("custom_param6")
    public String customParam6;
    @JsonProperty("custom_param7")
    public String customParam7;
    @JsonProperty("custom_param8")
    public String customParam8;
    @JsonProperty("custom_param9")
    public String customParam9;
    @JsonProperty("custom_param10")
    public String customParam10;

    public String getCustomParam1() {
        return customParam1;
    }

    public void setCustomParam1(String customParam1) {
        this.customParam1 = customParam1;
    }

    public String getCustomParam2() {
        return customParam2;
    }

    public void setCustomParam2(String customParam2) {
        this.customParam2 = customParam2;
    }

    public String getCustomParam3() {
        return customParam3;
    }

    public void setCustomParam3(String customParam3) {
        this.customParam3 = customParam3;
    }

    public String getCustomParam4() {
        return customParam4;
    }

    public void setCustomParam4(String customParam4) {
        this.customParam4 = customParam4;
    }

    public String getCustomParam5() {
        return customParam5;
    }

    public void setCustomParam5(String customParam5) {
        this.customParam5 = customParam5;
    }

    public String getCustomParam6() {
        return customParam6;
    }

    public void setCustomParam6(String customParam6) {
        this.customParam6 = customParam6;
    }

    public String getCustomParam7() {
        return customParam7;
    }

    public void setCustomParam7(String customParam7) {
        this.customParam7 = customParam7;
    }

    public String getCustomParam8() {
        return customParam8;
    }

    public void setCustomParam8(String customParam8) {
        this.customParam8 = customParam8;
    }

    public String getCustomParam9() {
        return customParam9;
    }

    public void setCustomParam9(String customParam9) {
        this.customParam9 = customParam9;
    }

    public String getCustomParam10() {
        return customParam10;
    }

    public void setCustomParam10(String customParam10) {
        this.customParam10 = customParam10;
    }
}

