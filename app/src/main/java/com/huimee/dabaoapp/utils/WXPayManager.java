package com.huimee.dabaoapp.utils;

import android.content.Context;
import android.util.Log;


import com.google.gson.Gson;
import com.huimee.dabaoapp.bean.WxPayBean;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者： GeekBin
 * 日期： 2017/1/17 15:16
 * 微信支付的管理类
 */

public class WXPayManager {
    private static final String TAG = "WXPay";

    private static WXPayManager mWXPay;
    private IWXAPI mWXApi;
    private String mPayParam;
    private WXPayResultCallBack mCallback;


    private long timeStamp;
    private String appId, partnerId, nonceStr, reqPackage, prePayId, sign;

    public static final int NO_OR_LOW_WX = 1;   //未安装微信或微信版本过低
    public static final int ERROR_PAY_PARAM = 2;  //支付参数错误
    public static final int ERROR_PAY = 3;  //支付失败

    public interface WXPayResultCallBack {
        void onSuccess(); //支付成功

        void onError(int error_code);   //支付失败

        void onCancel();    //支付取消
    }

    private WXPayManager(Context context, String wx_appid) {
        mWXApi = WXAPIFactory.createWXAPI(context, null);
        //        init(context,wx_appid);
        mWXApi.registerApp(wx_appid);
    }

    public static void init(Context context, String wx_appid) {
        if (mWXPay == null) {
            Log.d(TAG, "初始化init返回的数据");
            mWXPay = new WXPayManager(context, wx_appid);
        }
    }

    public static WXPayManager getInstance() {
        return mWXPay;
    }

    public IWXAPI getWXApi() {
        return mWXApi;
    }

    /**
     * 发起微信支付
     */
    public void doPay(String pay_param, WXPayResultCallBack callback) {
        Log.d(TAG, "doPay返回的数据");
        mPayParam = pay_param;
        mCallback = callback;

        if (!check()) {
            if (mCallback != null) {
                mCallback.onError(NO_OR_LOW_WX);
            }
            return;
        }

        JSONObject param = null;
        try {
            param = new JSONObject(mPayParam);
        } catch (JSONException e) {
            e.printStackTrace();
            if (mCallback != null) {
                mCallback.onError(ERROR_PAY_PARAM);
            }
            return;
        }
        if (param == null) {
            if (mCallback != null) {
                mCallback.onError(ERROR_PAY_PARAM);
            }
            return;
        }
        WxPayBean mallWxBean = new Gson().fromJson(pay_param, WxPayBean.class);
        appId = mallWxBean.getResponse().getAppid();//应用id
        partnerId = mallWxBean.getResponse().getPartnerid();//商户id
        nonceStr = mallWxBean.getResponse().getNoncestr();//32位随机数
        timeStamp = mallWxBean.getResponse().getTimestamp();//时间戳
        reqPackage = mallWxBean.getResponse().getPackageX();//扩展字段
        prePayId = mallWxBean.getResponse().getPrepayid();//预支付订单
        sign = mallWxBean.getResponse().getSign();//后台返回的签名


        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prePayId;
        req.packageValue = reqPackage;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp + "";
        req.sign = sign;
        Log.d(TAG, "appid返回的数据" + req.appId);
        boolean b = mWXApi.sendReq(req);
        Log.d(TAG, "sendReq" + b);
    }

    //支付回调响应
    public void onResp(int error_code) {
        if (mCallback == null) {
            return;
        }

        if (error_code == 0) {   //成功
            Log.d(TAG, "WXPayManager返回的数据url----onSuccess");
            mCallback.onSuccess();
        } else if (error_code == -1) {   //错误
            Log.d(TAG, "WXPayManager返回的数据url----onError");
            mCallback.onError(ERROR_PAY);
        } else if (error_code == -2) {   //取消
            Log.d(TAG, "WXPayManager返回的数据url----onCancel");
            mCallback.onCancel();
        }

        mCallback = null;
    }

    //检测是否支持微信支付
    private boolean check() {
        return mWXApi.isWXAppInstalled() && mWXApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }


}
