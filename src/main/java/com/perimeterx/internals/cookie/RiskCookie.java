package com.perimeterx.internals.cookie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskCookie {
    @JsonProperty("t")
    public long timestamp;
    @JsonProperty("h")
    public String hash;
    @JsonProperty("s")
    public RiskScore score;
    @JsonProperty("u")
    public String uuid;
    @JsonProperty("v")
    public String vid;

    @Override
    public String toString() {
        return "RiskCookie{" +
                "Timestamp='" + timestamp + '\'' +
                ", hash='" + hash + '\'' +
                ", Score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskCookie that = (RiskCookie) o;
        return timestamp == that.timestamp &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(score, that.score) &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(vid, that.vid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, hash, score, uuid, vid);
    }

}
