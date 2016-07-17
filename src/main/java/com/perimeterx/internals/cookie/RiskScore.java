package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RiskScore {

    @JsonProperty("a")
    public int application;
    @JsonProperty("b")
    public int bot;

    @Override
    public String toString() {
        return "RiskScore{" +
                "Application=" + application +
                ", Bot=" + bot +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskScore riskScore = (RiskScore) o;
        return application == riskScore.application &&
                bot == riskScore.bot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(application, bot);
    }
}
