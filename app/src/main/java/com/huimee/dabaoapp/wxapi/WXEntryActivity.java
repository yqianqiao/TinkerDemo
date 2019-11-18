

/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */



/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.huimee.dabaoapp.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.huimee.dabaoapp.config.Constants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.simple.eventbus.EventBus;


/** 微信客户端回调activity示例 */
public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
	public static final String TAG = "WXEntryActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			Constants.wx_api.handleIntent(getIntent(), this);
		}catch (Exception e){
			e.printStackTrace();
		}

	}



	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		Constants.wx_api.handleIntent(intent, this);
	}

	//微信请求相应
	@Override
	public void onReq(BaseReq baseReq) {
		Log.e(TAG, "onReq: =====================================" );
		switch (baseReq.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//				goToGetMsg();
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//				goToShowMsg((ShowMessageFromWX.Req) req);
				break;
			default:
				break;
		}
		finish();
	}

	//发送到微信请求的响应结果
	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				Log.i("WXTest","onResp OK");
				if(resp instanceof SendAuth.Resp){
					SendAuth.Resp newResp = (SendAuth.Resp) resp;
					//获取微信传回的code
					String code = newResp.code;
					Log.i("WXTest","onResp code = "+code);

					getThirdCallBack(code);
				}
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Log.i("WXTest","onResp ERR_USER_CANCEL ");
				//发送取消
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				Log.i("WXTest","onResp ERR_AUTH_DENIED");
				//发送被拒绝
				break;
			default:
				Log.i("WXTest","onResp default errCode " + resp.errCode);
				//发送返回
				break;
		}
		finish();
	}

	/**
	 * 第三方登录响应
	 *
	 * @param
	 */
	private void getThirdCallBack(String code) {

		EventBus.getDefault().post(code, "aaa");


//		String 	mUrl = Constants.BASEURL + Constants.LOGIN;
//		OkHttpUtils.post().url(mUrl)
//				.addParams("type", "weixin")//
//				.addParams("terminal", "mobile")
//				.addParams("weixincode", code)
//				.addParams("app_wx_v2", "1")
//				.build().execute(new StringCallback() {
//
//			@Override
//			public void onError(Call call, Exception e, int id) {
//				ToastUtil.showLong(WXEntryActivity.this, getResources().getString(R.string.netword_conect));
//			}
//
//			@Override
//			public void onResponse(String response, int id) {
//				if (!TextUtils.isEmpty(response)) {
//					try {
//						Log.d(TAG, "登录返回的数据" + response);
//						JSONObject object = new JSONObject(response);
//						String code = object.getString("code");
//						String message = object.getString("message");
//						ToastUtil.showLong(WXEntryActivity.this, message);
//						if (code.equals("1")) {
//							JSONObject responses = object.getJSONObject("response");
//						    String uid = responses.optString("uid");
//							String token = responses.optString("token");
//							SpCache.putString(SpCache.USERID, uid);
//							SpCache.putString(SpCache.TOKEN, token);
//							SpCache.putBoolean(SpCache.LOGINSTATE, true);
//							EventBus.getDefault().post("WXEntryActivity", WXEntryActivity.TAG);//向DaBaoActivity发送事件
//							Map<String, String> map = new Hashtable<String, String>();
//							map.put("type", "DaBaoActivity");
//							map.put("code", code);
//                            map.put("token", token);
//                            map.put("uid", uid);
//							EventBus.getDefault().post(map);
//							finish();
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//		});
	}



}
