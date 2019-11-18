package com.huimee.dabaoapp.bean;

/**
 * Created by YX on 2019/8/6 18:40.
 */
public class UpAPK {

    /**
     * response : {"android_version2":1,"androidextralink":""}
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
         * android_version2 : 1
         * androidextralink :
         */

        private int android_version2;
        private String androidextralink;

        public int getAndroid_version2() {
            return android_version2;
        }

        public void setAndroid_version2(int android_version2) {
            this.android_version2 = android_version2;
        }

        public String getAndroidextralink() {
            return androidextralink;
        }

        public void setAndroidextralink(String androidextralink) {
            this.androidextralink = androidextralink;
        }
    }
}
