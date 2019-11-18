package com.huimee.dabaoapp.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 */

public class InstallCountBean {

    /**
     * response : []
     * code : 1
     * message : 操作成功
     * count : 0
     */

    private int code;
    private String message;
    private int count;
    private List<?> response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<?> getResponse() {
        return response;
    }

    public void setResponse(List<?> response) {
        this.response = response;
    }
}
