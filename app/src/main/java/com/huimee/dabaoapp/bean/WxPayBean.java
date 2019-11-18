package com.huimee.dabaoapp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XY on 2018/5/3.
 */

public class WxPayBean {

    /**
     * response : {"appid":"wx0ff3f368f7a53498","partnerid":"1502622701","prepayid":"wx03102837775892389869d99e4234233384","package":"Sign=WXPay","noncestr":"V5zyHbGTwH","timestamp":1525314476,"sign":"3D90CB7464F66C98260EAD2263C7B868"}
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
         * appid : wx0ff3f368f7a53498
         * partnerid : 1502622701
         * prepayid : wx03102837775892389869d99e4234233384
         * package : Sign=WXPay
         * noncestr : V5zyHbGTwH
         * timestamp : 1525314476
         * sign : 3D90CB7464F66C98260EAD2263C7B868
         */

        private String appid;
        private String partnerid;
        private String prepayid;
        @SerializedName("package")
        private String packageX;
        private String noncestr;
        private int timestamp;
        private String sign;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
