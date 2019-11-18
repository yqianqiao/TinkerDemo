package com.huimee.dabaoapp.bean;

/**
 * Created by Administrator on 2018/6/29.
 */

public class DownlloadLinkBean {

    /**
     * response : {"ios":"http://api.sooyooj.com/index/app/download?t=ios","android":"http://api.sooyooj.com/index/app/download?t=android","extra_android":"http://sooyooj.com"}
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
         * ios : http://api.sooyooj.com/index/app/download?t=ios
         * android : http://api.sooyooj.com/index/app/download?t=android
         * extra_android : http://sooyooj.com
         */

        private String ios;
        private String android;
        private String extra_android;

        public String getIos() {
            return ios;
        }

        public void setIos(String ios) {
            this.ios = ios;
        }

        public String getAndroid() {
            return android;
        }

        public void setAndroid(String android) {
            this.android = android;
        }

        public String getExtra_android() {
            return extra_android;
        }

        public void setExtra_android(String extra_android) {
            this.extra_android = extra_android;
        }
    }
}
