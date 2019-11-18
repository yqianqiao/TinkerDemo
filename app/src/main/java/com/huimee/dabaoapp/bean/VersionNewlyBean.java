package com.huimee.dabaoapp.bean;

/**
 * Created by XY on 2018/5/14.
 */

public class VersionNewlyBean {

    /**
     * response : {"url":""}
     * code : 1
     * message : 操作成功
     * count : 0
     */

    private ResponseBean response;
    private int code;
    private String message;
    private int count;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

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

    public static class ResponseBean {
        /**
         * url :
         */

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
