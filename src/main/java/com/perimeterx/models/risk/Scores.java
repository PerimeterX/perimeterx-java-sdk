package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Scores model
 * <p>
 * Created by shikloshi on 04/07/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Scores {

    private int filter;
    @JsonProperty("non_human")
    private int nonHuman;
    @JsonProperty("suspected_script")
    private int suspectedScript;

    public Scores() {
    }

    public Scores(int filter, int nonHuman, int suspectedScript) {
        this.filter = filter;
        this.nonHuman = nonHuman;
        this.suspectedScript = suspectedScript;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public int getNonHuman() {
        return nonHuman;
    }

    public void setNonHuman(int nonHuman) {
        this.nonHuman = nonHuman;
    }

    public int getSuspectedScript() {
        return suspectedScript;
    }

    public void setSuspectedScript(int suspectedScript) {
        this.suspectedScript = suspectedScript;
    }

}
