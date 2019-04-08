package com.qdf.rsa_demo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonResult implements Serializable {

    public static final Integer JSON_RESULT_SUCCESS = Integer.valueOf(1);
    public static final Integer JSON_RESULT_FAILED = Integer.valueOf(0);

    private final List<Object> data = new ArrayList();
    private Integer returnCode;
    private String msg;

    protected JsonResult() {
    }

    private JsonResult(Integer returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = msg;
    }

    public static JsonResult createSuccess() {
        return new JsonResult(JSON_RESULT_SUCCESS, (String)null);
    }
    public static JsonResult createSuccess(String msg) {
        return new JsonResult(JSON_RESULT_SUCCESS, msg);
    }
    public static JsonResult create(Integer returnCode, String msg) {
        return new JsonResult(returnCode, msg);
    }

    public static JsonResult createFalied(String msg) {
        JsonResult jsonResult = new JsonResult(JSON_RESULT_FAILED, msg);
        return jsonResult;
    }
    public Integer getReturnCode() {
        return this.returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Object> getData() {
        return this.data;
    }

    public void addData(Object obj) {
        this.data.add(obj);
    }

    public void removeDataAll() {
        if(this.data != null) {
            this.data.clear();
        }

    }

    public void addDataAll(List list) {
        if(null != list) {
            this.data.addAll(list);
        }

    }

}
