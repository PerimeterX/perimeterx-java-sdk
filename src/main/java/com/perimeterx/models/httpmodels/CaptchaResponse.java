package com.perimeterx.models.httpmodels;

/**
 * CaptchaResponse model
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class CaptchaResponse {

    private int status;
    private String uuid;
    private String vid;
    private String cid;

    public CaptchaResponse() {
    }

    public CaptchaResponse(int status, String uuid, String vid, String cid) {
        this.status = status;
        this.uuid = uuid;
        this.vid = vid;
        this.cid = cid;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getStatus() {
        return status;
    }

    public String getUuid() {
        return uuid;
    }

    public String getVid() {
        return vid;
    }

    public String getCid() {
        return cid;
    }
}
