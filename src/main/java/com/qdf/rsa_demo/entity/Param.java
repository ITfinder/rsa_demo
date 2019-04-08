package com.qdf.rsa_demo.entity;

import java.io.Serializable;

/**
 * 入参实体类
 */
public class Param implements Serializable {
    private String wid;
    private String token;
    private String timestamp;
    private String afterRsa;

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAfterRsa() {
        return afterRsa;
    }

    public void setAfterRsa(String afterRsa) {
        this.afterRsa = afterRsa;
    }
}
