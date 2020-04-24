package com.huimee.dabaoapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cecil.okhttp.CallBack;
import com.cecil.okhttp.OkHttpManage;
import com.google.gson.Gson;
import com.huimee.dabaoapp.base.MyBaseActivity;
import com.huimee.dabaoapp.bean.ActiveBean;
import com.huimee.dabaoapp.bean.DownlloadLinkBean;
import com.huimee.dabaoapp.bean.InstallCountBean;
import com.huimee.dabaoapp.bean.UpAPK;
import com.huimee.dabaoapp.bean.WxPayBean;
import com.huimee.dabaoapp.config.Constants;
import com.huimee.dabaoapp.database.SpCache;
import com.huimee.dabaoapp.reciever.ShortcutsReciever;
import com.huimee.dabaoapp.ui.dialog.VersionActiveDialog;
import com.huimee.dabaoapp.utils.FileUtils;
import com.huimee.dabaoapp.utils.ToastUtil;
import com.huimee.dabaoapp.utils.WXPayManager;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.huimee.dabaoapp.app.MyApplication.QQ_APP_ID;
import static com.huimee.dabaoapp.app.MyApplication.WX_APP_ID;
import static com.huimee.dabaoapp.app.MyApplication.mUserAgent;

/**
 * @author XY
 * @date 2018/5/25
 */

public class DaBaoActivity extends MyBaseActivity {
    public static final String TAG = "DaBaoActivity";


    //设置缓存webview的路径
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    @InjectView(R.id.webView1)
    FrameLayout webView1;
    @InjectView(R.id.iv_loading)
    ImageView ivLoading;

    private static final int PERMISSION_REQUESTCODE = 1;
    @InjectView(R.id.tv_refresh)
    TextView tvRefresh;
    @InjectView(R.id.ll_net)
    LinearLayout llNet;
    @InjectView(R.id.login)
    Button login;
    @InjectView(R.id.ll_item)
    RelativeLayout llItem;
    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    private WebView mWebView;
    private Handler mHandler = new Handler();
    private String id, s;
    private int type, ifTheCallback;
    private DownlloadLinkBean downlloadLinkBean;
    final public static int WRITE_EXTERNAL_STORAGE_RESULT_CODE = 123;
    private Date aDate, bDate;
    private int num;
    private int version;
    private Bitmap temBitmap;
    private Tencent mTencent;

    private Boolean isOne;
    private SharedPreferences sharedPreferences;
    private String sid;
    private Long time;
    private String CookieStr;
    private String uid;
    private String token;
    private int Z = 0;

    @Override
    protected void findById() {

        String name = "";
        int code = 0;
        PackageManager mPackageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(this.getPackageName(), 0);
            name = packageInfo.packageName;
            code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        new AlertDialog.Builder(this)
//                .setMessage("WX_APP_ID :" + WX_APP_ID + "\n"
//                        + "APPLICATION_ID :" + BuildConfig.APPLICATION_ID + "\n"
//                        + "ID :" + BuildConfig.id + "\n"
//                        + "包名 :" + name + "\n"
//                        + "版本 :" + code + "\n"
//                        + "S :" + BuildConfig.s + "\n")
//                .show();

        //创建微信api并注册到微信
        Constants.wx_api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        Constants.wx_api.registerApp(WX_APP_ID);

        mTencent = Tencent.createInstance(QQ_APP_ID, getApplicationContext());
        EventBus.getDefault().register(this);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

    @Override
    protected void init() {
        version = getVersion();
        aDate = new Date();
    }

    @Override
    protected void loadData() {


        sid = getIntent().getStringExtra("id"); //官方包3
        id = sid;
        s = getIntent().getStringExtra("s");
        uid = getIntent().getStringExtra("uid");
        token = getIntent().getStringExtra("token");

//        this.id = BuildConfig.id.toString();
//        this.s = BuildConfig.s.toString();

//
//        new AlertDialog.Builder(this)
//                .setMessage(
//                        "token :" + token + "\n")
//                .show();


//        if (Util.checkApkExist(this, "com.huimee.dabaoappplus")) {
//            Intent intent = getPackageManager().getLaunchIntentForPackage("com.huimee.dabaoappplus");
//            intent.putExtra("id", id);
//            intent.putExtra("s", s);
//            intent.putExtra("token", token);
//            startActivity(intent);
////            ComponentName componetName = new ComponentName(
////                    "com.huimee.dabaoappplus",
////                    "com.huimee.dabaoappplus.DaBaoActivity");
////            //（另外一个应用程序的包名，要启动的Activity ）
////            Intent intent = new Intent();
////            intent.putExtra("id", id);
////            intent.putExtra("s", s);
////            intent.putExtra("uid", uid);
////            intent.putExtra("token", token);
////            intent.setComponent(componetName);
////            startActivity(intent);
//            finish();
//        }

//        http://j.hbwcl.com/index/game/count?id=52&s=741&c={uid}
//        http://192.168.1.106:80/play.html?id=15

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //http://j.hbwcl.com/index/game/count?id=15&s=824&c={uid

        Log.e(TAG, "loadData:pack" );

        //渠道ID
        SpCache.putString(SpCache.QD_ID, BuildConfig.s.toString());//做版本更新的时候记得把这行注释掉
        //游戏ID
//        this.id = BuildConfig.id.toString();
//        //渠道ID
//        s = SpCache.getQdId();//这行不要改

//        http://j.hbwcl.com/index/game/count?id=84&s=1185&c={uid    86400000
//        游戏包
//        isOne = sharedPreferences.getBoolean("isOne", false);
//        if (!Util.checkApkExist(this, "com.huimee.dabaoappplus") && !TextUtils.equals("com.huimee.dabaoappplus", BuildConfig.APPLICATION_ID) && 86400000 < System.currentTimeMillis() - sharedPreferences.getLong("time", System.currentTimeMillis())) {
//            UpAPK.ResponseBean upapk = new UpAPK.ResponseBean();
//            upapk.setAndroidextralink("http://v.heygugu.com/CPAxz/Sooyooj_Android.apk");
//            VersionActiveDialog cleanCacheDialog = new VersionActiveDialog(DaBaoActivity.this, upapk, DaBaoActivity.this, "guanbao");
//            cleanCacheDialog.setCanceledOnTouchOutside(false);//dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
//            // cleanCacheDialog.setCancelable(false);// dialog弹出后会点击屏幕或物理返回键，dialog不消失
//            cleanCacheDialog.show();
//        }

//        if (!isOne) {
//            Log.e(TAG, "================ ");
//        sharedPreferences.edit().putLong("time", System.currentTimeMillis()).apply();
        new Handler().postDelayed(() -> clipboardAndroid(DaBaoActivity.this), 3000);

        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("s", s);
        OkHttpManage.get("http://jg.sooyooj.com/index/game/count", map, new CallBack() {
            @Override
            public void onError(String s) {
            }

            @Override
            public void onResponse(String s) {
                sharedPreferences.edit().putBoolean("isOne", true).apply();
            }
        });
//        hideStatusBar(DaBaoActivity.this);
        goneSystemUi();

        //链接类型：
        // 如果是http://sooyooj.com/play.html?id="+id+"&s="+s的格式  type = 1
        // 如果是http://j.hbwcl.com/index/game/count?id="+id+"&s="+s+"&c={uid}的格式  type = 0
        type = 1;
        Log.e(TAG, "loadData: ");
        //是否开启回调：如果是开启回调  ifTheCallback = 1   如果是不开启回调  ifTheCallback = 0
        ifTheCallback = 0;
        permission();
        versionActive();
        initView2();
//        clipboardAndroid(this);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "正在刷新，请稍后", Toast.LENGTH_LONG).show();
                mWebView.reload();
            }
        });

    }

    //window.webkit.messageHandlers.hiddenStatusAndTabbar.postMessage(true);
    @Override
    protected int setLayoutId() {
        return R.layout.activity_dabao;
    }

    private Boolean mIsLoadSuccess = true;


    @SuppressLint("JavascriptInterface")
    private void initView2() {
//        mWebView = new X5WebView(this, null);
        mWebView = new WebView(this, null);
        webView1.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.e(TAG, "shouldOverrideUrlLoading: 测试" );
                if (url.contains("wxpay")) {
                    getWxPay(url);
                    return true;
                } else if (url.contains("alipay") || url.contains("download.html")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                    return true;
                } else if (url.contains("https://open.weixin.qq.com")) {
                    //发起登录请求
                    final SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_sdk_demo_test";
                    boolean b = Constants.wx_api.sendReq(req);
                    Log.e(TAG, "shouldOverrideUrlLoading: " + b);


                    return true;
                } else if (url.contains("jq.qq.com") || url.contains("qm.qq.com")) {//礼包
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                    ToastUtil.showMyToast(6 * 1000, mContext, "若无法跳转，请复制链接在qq或微信打开");
                    return true;
                } else if (url.contains("baidu.com")) {
                    popShotSrceenDialog();//屏幕截图
                    if (!"2".equals(SpCache.getSendMacTwo())) {
                        sendMac(view);//发送mac
                    }

                    return true;
                } else if (url.contains("share,")) {
                    showBottomDialog(url);
                    return true;

                } else if (url.contains("&regmac=1")) {//首次注册启用回调
                    if (!"2".equals(SpCache.getRegisterTwo())) {
                        activeRegister(getDeviceId(mContext));
                    }
                    return true;
                } else if (url.contains("https://graph.qq.com/oauth2.0")) {//QQ登录
                    //QQ第三方登录
                    //1106782196       101441019     这里的“123123123”改为自己的Appid
                    mTencent.login(DaBaoActivity.this, "all", BaseUiListener);
                    TinkerPatch.with().fetchPatchUpdate(true);
                    return true;
                } else if (url.contains("locationtype")) {
//                    Uri uri = Uri.parse(url);
//                    String uid = uri.getQueryParameter("uid");
//                    String token = uri.getQueryParameter("token");
//                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.huimee.dabaoappplus");
//                    intent.putExtra("id", id);
//                    intent.putExtra("s", s);
//                    intent.putExtra("uid", uid);
//                    intent.putExtra("token", token);
//                    startActivity(intent);
//                    finish();
                    return true;
                } else {
                    return false;
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ivLoading.setVisibility(View.VISIBLE);
                webView1.setVisibility(View.GONE);
                Glide.with(mContext).load(R.mipmap.loading).into(ivLoading);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                llNet.setVisibility(View.VISIBLE);
                webView1.setVisibility(View.GONE);
                mIsLoadSuccess = false;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
//                String js = "window.localStorage.getItem('uid');";
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    view.evaluateJavascript(js, new ValueCallback() {
//                        @Override
//                        public void onReceiveValue(Object value) {
//
//                            Log.e("Cookies123", "VALUE--->" + value);
//                        }
//                    });
//                } else {
//                    view.loadUrl("");//暂时不支持
//                    view.reload();
//                }

//                CookieManager cookieManager = CookieManager.getInstance();
//                CookieStr = cookieManager.getCookie("http://sooyooj.com/play.html");
//                Log.i("Cookies", "Cookies = " + CookieStr);
//                if (!TextUtils.isEmpty(CookieStr)) {
//                    token = CookieStr.replace(";", "&");
//                }
//                if (Util.checkApkExist(DaBaoActivity.this, "com.huimee.dabaoappplus")) {
//                    mWebView.loadUrl("javascript:locatFun(1)");
//                }


//                if (!mIsLoadSuccess) {
//                    llNet.setVisibility(View.VISIBLE);
//                    webView1.setVisibility(View.GONE);
//                    mIsLoadSuccess = true;
//                } else {
//                    llNet.setVisibility(View.GONE);
//                    webView1.setVisibility(View.VISIBLE);
//                    view.getUrl();
//                    WebSettings webSetting = view.getSettings();
//                    // 获取到UserAgentString
//                    String userAgent = webSetting.getUserAgentString();
////                if (!userAgent.contains("MQQBrowser")) {
////                    restartApplication();//如果没有检测到腾讯X5内核就重启应用（部分手机第一次打开检测不到腾讯X5内核，会导致游戏无声音）
////                }
//                }
            }

        });
        TinkerPatch.with().fetchDynamicConfig(new ConfigRequestCallback() {
            @Override
            public void onSuccess(HashMap<String, String> hashMap) {
                index = hashMap.get("text");
            }

            @Override
            public void onFail(Exception e) {

            }
        }, false);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(null, url, message, result);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress < num) {//如果第二次load就开始时间清零重新计算。
                    aDate = new Date();
                }
                num = newProgress;
                bDate = new Date();
                long interval = (bDate.getTime() - aDate.getTime()) / 1000;//获取时间差

                if (interval > 5 && interval <= 35) {
                    ToastUtil.showLong(mContext, "努力加载中。。。请耐心等待");
                }

                if (interval > 35) {
                    ToastUtil.showMyToast(6 * 1000, mContext, "请重新进入游戏或者切换网络后再试！");
                    finish();
                }

                if (newProgress > 70) {
                    ivLoading.setVisibility(View.GONE);
                    webView1.setVisibility(View.VISIBLE);
                    // getProgressDialog("玩命加载中.....").dismiss();
                } else {
                    ivLoading.setVisibility(View.VISIBLE);
                    webView1.setVisibility(View.GONE);
                    Glide.with(mContext).load(R.mipmap.loading).into(ivLoading);
                    // getProgressDialog("玩命加载中.....").show();
                }


//                ivLoading.setVisibility(View.VISIBLE);
//                webView1.setVisibility(View.GONE);
//
//
//                Glide.with(mContext).load(R.mipmap.loading).into(ivLoading);
//
//                int r = new Random().nextInt(5);
//                if (r == 1) {
//                    int s = new Random().nextInt(5) + 10;
//                    new Handler().postDelayed(() -> {
//                        ivLoading.setVisibility(View.GONE);
//                        webView1.setVisibility(View.VISIBLE);
//                    }, s * 1000L);
//
//                } else if (r == 4) {
//                    ivLoading.setVisibility(View.VISIBLE);
//                    webView1.setVisibility(View.GONE);
//                    Glide.with(mContext).load(R.mipmap.loading).into(ivLoading);
//                }else {
//                    int s = new Random().nextInt(4) + 2;
//                    new Handler().postDelayed(() -> {
//                        ivLoading.setVisibility(View.GONE);
//                        webView1.setVisibility(View.VISIBLE);
//                    }, s * 1000L);
//                }

//                setVisibility(newProgress > 70);
//                if (newProgress > 70) {
//                    ivLoading.setVisibility(View.GONE);
//                    webView1.setVisibility(View.VISIBLE);
//                    // getProgressDialog("玩命加载中.....").dismiss();
//                } else {
//                    ivLoading.setVisibility(View.VISIBLE);
//                    webView1.setVisibility(View.GONE);
//                    Glide.with(mContext).load(R.mipmap.loading).into(ivLoading);
//                    // getProgressDialog("玩命加载中.....").show();
//                }
////                cutDialog.setView(view);
//                Window window = cutDialog.getWindow();
//                window.setBackgroundDrawableResource(android.R.color.transparent);
//                WindowManager m = window.getWindowManager();
//                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//                WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
//                p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
//                p.gravity = Gravity.CENTER;//设置弹出框位置
//                window.setAttributes(p);
//                window.setWindowAnimations(R.style.dialogWindowAnim);
//                cutDialog.show();
//                setVisibility(newProgress > 70);

            }
        });

        WebSettings webSetting = mWebView.getSettings();

        // 获取到UserAgentString
        String userAgent = webSetting.getUserAgentString();
        if (userAgent.contains("MQQBrowser")) {//如果是腾讯X5内核就把他标记成第二次
            SpCache.putString(SpCache.IF_TBS, "2");
        }

//        if (!"2".equals(SpCache.getIfTbs())) {//第一次打开APP
//
//            ToastUtil.showMyToast(6 * 1000, mContext, "首次打开初始化请等待5秒");
//            // ToastUtil.showMyToast(6*1000,mContext,"升级完毕请重新打开游戏");
//        } else {
//
//            ToastUtil.showMyToast(6 * 1000, mContext, "当前版本号：1_" + version);
//        }
        Log.e(TAG, "userAgent: " + userAgent + mUserAgent);
        webSetting.setUserAgentString(userAgent + mUserAgent);//UA跟前端约定好的，乱改会导致屏幕截图和某些功能无法使用，改之前跟前端协商好

        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setAppCacheEnabled(true);

        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
//        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
//        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
//                .getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        long time = System.currentTimeMillis();


        //下面两行解决白屏
//        mWebView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
//         mWebView.setBackgroundResource(R.mipmap.loading);
////         mWebView.setBackground(R.mipmap.ic_applog);
//          mWebView.loadUrl("http://j.hbwcl.com/index/game/count?id="+id+"&s="+s+"&c={uid}");//"http://sooyooj.com/play.html?id="+id+"&s="+s
        type(type);
//        mWebView.loadUrl("http://192.168.1.101:80/play.html?id=15");
//        mWebView.loadUrl("http://www.sooyooj.com/play.html?id=69&s=1125&c={uid}");
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

    }


    private void type(int num) {
        mWebView.loadUrl("http://www.sooyooj.com/index.html");
//        mWebView.loadUrl("http://192.168.0.105:81/index.html");
//        mWebView.loadUrl("http://192.168.0.188:80/index.html");
//        mWebView.loadUrl("http://tg.sooyooj.com/?u=1018081");
//
//        if (TextUtils.isEmpty(sid)) {
//            mWebView.loadUrl("http://www.sooyooj.com/index.html");
//        } else {
//            String s = "http://www.sooyooj.com/play.html?id=" + id + "&s=" + this.s + "&uid=" + uid + "&token=" + token;
//            mWebView.loadUrl(s);
//        }

//        mWebView.loadUrl("http://www.sooyooj.com/index.html");
//        mWebView.loadUrl("http://www.sooyooj.com/index.html?id=" + id + "&s=" + this.s + "&uid=" + uid + "&token=" + token);
//        if (TextUtils.isEmpty(sid)) {
//
//        } else {
////            mWebView.loadUrl("javascript:androidlocationhref(\"" + id + "\",\"" + this.s + "\",\"" + uid + "\",\"" + token + "\")");
//
//        }

//
//        if (num == 1) {
//            //外网的
////            mWebView.loadUrl("http://tg.sooyooj.com/?u=423887");
//            mWebView.loadUrl("http://sooyooj.com/play.html?id=" + id + "&s=" + s);
////            mWebView.loadUrl("http://192.168.0.105:80/play.html?id=" + id + "&s=" + s);
//        } else if (num == 0) {
//            //http://j.hbwcl.com/index/game/count?id=44&s=579&c={uid}  http://www.sooyooj.com/play.html?id=84&s=1185
//            mWebView.loadUrl("http://j.hbwcl.com/index/game/count?id=" + id + "&s=" + s + "&c={uid}");
//        }
//        OkHttpManage.get().url("http://mb.12365chia.com/iplog/oj.php?z=0001")
//                .build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                Log.e(TAG, "onError: " + e);
//                mWebView.loadUrl("http://tg.sooyooj.com/?u=423887");
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                Log.e(TAG, "onResponse: " + response);
//                GameBean gameBean = new Gson().fromJson(response, GameBean.class);
//                mWebView.loadUrl(gameBean.getUrl());
//            }
//        });

    }

    private void wxPay(String response) {
        //微信支付获取参数
        WXPayManager.init(mContext, WX_APP_ID);
        WXPayManager.getInstance().doPay(response, new WXPayManager.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                ToastUtil.showLong(mContext, "支付成功");
            }

            @Override
            public void onError(int error_code) {
                ToastUtil.showLong(mContext, "支付失败，请重试");
            }

            @Override
            public void onCancel() {
                ToastUtil.showLong(mContext, "支付取消");
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView.destroy();//防止WebView加载内存泄漏
            // mWebView = null;
        }
        EventBus.getDefault().unregister(this);
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();

        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();

    }


    /**
     * 获取微信支付参数
     */
    private void getWxPay(String mUrl) {
        Map<String, String> map = new HashMap<>();
        map.put("app_wx_v2", "1");
        map.put("appid", WX_APP_ID);
        OkHttpManage.get(mUrl, map, new CallBack() {
            @Override
            public void onError(String s) {

            }

            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        Log.d(TAG, "获取微信支付参数返回的数据" + s);
                        String string = s.toString();
                        WxPayBean wxPayBean = new Gson().fromJson(string, WxPayBean.class);
                        if (wxPayBean.getCode() == 1) {
                            Log.d(TAG, "去调微信支付返回的数据" + s);
                            wxPay(string);
                        } else {
                            ToastUtil.showLong(mContext, wxPayBean.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showLong(mContext, s);
                    }
                } else {
                    ToastUtil.showLong(mContext, "服务器异常");
                }
            }
        });

    }


    /**
     * 处理WXEntryActivity发送过来的事件
     */
    @Subscriber(mode = ThreadMode.MAIN)
    public void wxLogin(Map<String, String> map) {
        if (map != null) {
            String type = map.get("type");
            //判断修改哪个字段
            if (type.equals("DaBaoActivity")) {
                Log.d(TAG, "去调微信返回的数据code" + map.get("code"));

                mWebView.loadUrl("javascript:wx(" + map.get("code") + ")");
//创建CookieSyncManager
//                CookieSyncManager.createInstance(mContext);
//                //得到CookieManager
//                CookieManager cookieManager = CookieManager.getInstance();
//
//                String token = "token" + "=" + map.get("token");
//                String uid = "uid" + "=" + map.get("uid");
//
//                //调用微信登录成功，然后调后台登录接口拿到uid和token设置cookie去登录游戏
//                cookieManager.setAcceptCookie(true);
//                cookieManager.removeSessionCookie();//移除
//                cookieManager.setCookie("http://sooyooj.com/play.html?id=" + id + "&s=" + s, token);
//                cookieManager.setCookie("http://sooyooj.com/play.html?id=" + id + "&s=" + s, uid);
//                CookieSyncManager.getInstance().sync();
//                mWebView.loadUrl("http://sooyooj.com/play.html?id=" + id + "&s=" + s);
//                if (!"2".equals(SpCache.getSendMacTwo())) {//第一次才发送mac
//                    sendMac(mWebView);//发送mac
//                }

            }

        }

    }


    /**
     * 传mac
     *
     * @param view
     */
    private void sendMac(WebView view) {
        String mac = "";
        if (getLocalMacAddressFromIp() != null) {
            mac = getLocalMacAddressFromIp();
        }
        String str = getDeviceId(mContext) + "," + mac;
        view.loadUrl("javascript:getAndroidMsg(\"" + str + "\")");
        TinkerPatch.with().fetchPatchUpdate(true);
        SpCache.putString(SpCache.SEND_MAC_TWO, "2");
    }


    /**
     * 获取设备的唯一标识，deviceId
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = "";
        try {
            Log.d(TAG, "deviceId----ok返回的数据");
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
            Log.d(TAG, "deviceId----no返回的数据");
            e.printStackTrace();
            return "";
        }
        if (deviceId == null) {
            return "";
        } else {
            return deviceId;
        }
    }

    /**
     * APP安装完成后初次运行时调用此接口
     */
    private void installCount(String deviceId) {
        String mUrl = Constants.BASEURL + Constants.INSTALL_COUNT;
        Map<String, String> map = new HashMap<>();
        map.put("platform", "android");
        map.put("id", deviceId);
        map.put("channelid", s);
        OkHttpManage.post(mUrl, map, new CallBack() {
            @Override
            public void onError(String s) {
                ToastUtil.showLong(DaBaoActivity.this, getResources().getString(R.string.netword_conect));
            }

            @Override
            public void onResponse(String s) {
                Log.d(TAG, "APP安装完成后初次运行时调用此接口返回的数据" + s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        String string = s.toString();
                        InstallCountBean versionActiveBean = new Gson().fromJson(string, InstallCountBean.class);
                        if (versionActiveBean.getCode() == 1) {
                            SpCache.putString(SpCache.STATE_Two, "2");
                        } else {
                            ToastUtil.showLong(DaBaoActivity.this, versionActiveBean.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showLong(DaBaoActivity.this, "服务器异常");
                }
            }
        });

    }

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        Process.killProcess(Process.myPid());
        // System.exit(0);


    }

    /**
     * 激活数据回调操作
     */
    private void active(String deviceId) {
        //Toast.makeText(DaBaoActivity.this, "测试-------------", Toast.LENGTH_SHORT).show();
        String mUrl = "http://mb.12365chia.com/appv/cb.php?mac=" + getLocalMacAddressFromIp() + "&muid=" + deviceId + "&subid=" + s + "&event_type=0";
        OkHttpManage.post(mUrl, null, new CallBack() {
            @Override
            public void onError(String s) {
                ToastUtil.showLong(DaBaoActivity.this, getResources().getString(R.string.netword_conect));
            }

            @Override
            public void onResponse(String s) {
                Log.d(TAG, "激活数据回调操作返回的数据" + s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        String string = s.toString();
                        ActiveBean activeBean = new Gson().fromJson(string, ActiveBean.class);
                        if (!"".equals(activeBean.getActive_cb())) {
                            xxxxxx(activeBean.getActive_cb());
                        }
                        SpCache.putString(SpCache.REGISTER_TWO, "2");
                        SpCache.putString(SpCache.STATE_Two, "2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showLong(DaBaoActivity.this, "服务器异常");
                }
            }
        });


    }

    /**
     * 激活数据回调操作
     */
    private void activeRegister(String deviceId) {
        //Toast.makeText(DaBaoActivity.this, "测试-------------", Toast.LENGTH_SHORT).show();
        String mUrl = "http://mb.12365chia.com/appv/cb.php?mac=" + getLocalMacAddressFromIp() + "&muid=" + deviceId + "&subid=" + s + "&event_type=1";
        OkHttpManage.post(mUrl, null, new CallBack() {
            @Override
            public void onError(String s) {
                ToastUtil.showLong(DaBaoActivity.this, getResources().getString(R.string.netword_conect));
            }

            @Override
            public void onResponse(String s) {
                Log.d(TAG, "激活数据回调操作返回的数据" + s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        String string = s.toString();
                        ActiveBean activeBean = new Gson().fromJson(string, ActiveBean.class);
                        if (!"".equals(activeBean.getActive_cb())) {
                            xxxxxx(activeBean.getActive_cb());
                        }
                        SpCache.putString(SpCache.REGISTER_TWO, "2");
                        SpCache.putString(SpCache.STATE_Two, "2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showLong(DaBaoActivity.this, "服务器异常");
                }
            }
        });

    }

    /**
     * xxxxxxx
     */
    private void xxxxxx(String url) {
        OkHttpManage.get(url, null, new CallBack() {
            @Override
            public void onError(String s) {
                ToastUtil.showLong(DaBaoActivity.this, getResources().getString(R.string.netword_conect));
            }

            @Override
            public void onResponse(String s) {
                Log.d(TAG, "xxxxxxxxxxxx返回的数据" + s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showLong(DaBaoActivity.this, "服务器异常");
                }
            }
        });


    }

    private void permission() {
        List<String> permissionLists = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionLists.isEmpty()) {//说明肯定有拒绝的权限
            ActivityCompat.requestPermissions(this, permissionLists.toArray(new String[permissionLists.size()]), PERMISSION_REQUESTCODE);

        } else {
            //Toast.makeText(this, "权限都授权了，可以搞事情了", Toast.LENGTH_SHORT).show();
            if (!"2".equals(SpCache.getStateTwo())) {
//                installCount(getDeviceId(mContext));
                if (ifTheCallback == 1) {
                    active(getDeviceId(mContext));
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUESTCODE:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            ToastUtil.showMyToast(6 * 1000, mContext, "拒绝权限会导致功无法正常使用，可前往设置打开权限");
                            if (!"2".equals(SpCache.getStateTwo())) {
//                                installCount("");
                                if (ifTheCallback == 1) {
                                    active("");
                                }
                            }
                            return;
                        }
                    }
                    if (!"2".equals(SpCache.getStateTwo())) {
//                        installCount(getDeviceId(mContext));
                        if (ifTheCallback == 1) {
                            active(getDeviceId(mContext));
                        }
                    }
                }
                break;
            case 23:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!"2".equals(SpCache.getStateTwo())) {
//                        installCount(getDeviceId(mContext));
                        if (ifTheCallback == 1) {
                            active(getDeviceId(mContext));
                        }
                    }
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(DaBaoActivity.this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            Log.d(TAG, "getVersion: 客户端的版本号是的数据    --    " + version);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 检测安卓独立安装包是否可用
     * value 为1时表示此版本可用，为0时表示此版本不可用，需要更新
     */
    private void versionActive() {
        Map<String, String> map = new HashMap<>();
        map.put("gameid", "-1");
        map.put("version", getVersion() + "");
        OkHttpManage.post(Constants.BASEURL + Constants.UP_APK, map, new CallBack() {
            @Override
            public void onError(String s) {
                ToastUtil.showLong(DaBaoActivity.this, getResources().getString(R.string.netword_conect));
            }

            @Override
            public void onResponse(String s) {
                UpAPK upAPK = new Gson().fromJson(s, UpAPK.class);
                if (upAPK.getCode() == 1) {
                    if (upAPK.getResponse().getAndroid_version2() > getVersion()) {
                        VersionActiveDialog cleanCacheDialog = new VersionActiveDialog(DaBaoActivity.this, upAPK.getResponse(), DaBaoActivity.this, "dabao");
                        cleanCacheDialog.setCanceledOnTouchOutside(false);//dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
                        // cleanCacheDialog.setCancelable(false);// dialog弹出后会点击屏幕或物理返回键，dialog不消失
                        cleanCacheDialog.show();
                    }
                }
            }
        });

//        String mUrl = Constants.BASEURL + Constants.EXTRA_VERSION_ACTIVE;
//          OkHttpManage.post(Constants.BASEURL + Constants.UP_APK, map, new CallBack() {
//            @Override
//           public void onError(String s) {
//                ToastUtil.showLong(DaBaoActivity.this, getResources().getString(R.string.netword_conect));
//
//            }
//
//            @Override
//            public void onResponse(String s) {
//                if (!TextUtils.isEmpty(response)) {
//                    try {
//                        Log.d(TAG, "检测安卓独立安装包是否可用返回的数据" + response);
//                        String string = response.toString();
//                        VersionActiveBean versionActiveBean = new Gson().fromJson(string, VersionActiveBean.class);
//                        if (versionActiveBean.getCode() == 1) {
//                            if (versionActiveBean.getResponse().getV() == 0) {
//                                downlloadLink();
//                            }
//                        } else {
//                            ToastUtil.showLong(DaBaoActivity.this, versionActiveBean.getMessage());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    ToastUtil.showLong(DaBaoActivity.this, "服务器异常");
//                }
//            }
//
//
//        });

    }

    /**
     * 获取安卓最新版本的下载连接的接口
     */
    private void downlloadLink() {
        String mUrl = Constants.BASEURL + Constants.DOWNLLOAD_LINK;
        Map<String, String> map = new HashMap<>();
        map.put("gameid", id);
        OkHttpManage.post(mUrl, map, new CallBack() {
            @Override
            public void onError(String s) {
                ToastUtil.showLong(DaBaoActivity.this, getResources().getString(R.string.netword_conect));
            }

            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        Log.d(TAG, "获取安卓最新版本的下载连接返回的数据" + s);
                        String string = s.toString();
                        downlloadLinkBean = new Gson().fromJson(string, DownlloadLinkBean.class);
                        if (downlloadLinkBean.getCode() == 1) {
                            //直接调用更新方法
//                            VersionActiveDialog cleanCacheDialog = new VersionActiveDialog(DaBaoActivity.this, downlloadLinkBean, DaBaoActivity.this, "dabao");
//                            cleanCacheDialog.setCanceledOnTouchOutside(false);//dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
//                            // cleanCacheDialog.setCancelable(false);// dialog弹出后会点击屏幕或物理返回键，dialog不消失
//                            cleanCacheDialog.show();
                        } else {
                            ToastUtil.showLong(DaBaoActivity.this, downlloadLinkBean.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showLong(DaBaoActivity.this, "服务器异常");
                }
            }
        });

    }


    private void popShotSrceenDialog() {
 /*       final android.support.v7.app.AlertDialog cutDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        View dialogView = View.inflate(this, R.layout.show_cut_screen_layout, null);
        ImageView showImg = (ImageView) dialogView.findViewById(R.id.show_cut_screen_img);
        dialogView.findViewById(R.id.share_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.share_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"点击了share按钮",Toast.LENGTH_SHORT).show();
            }
        });*/
        //获取当前屏幕的大小
        int width = getWindow().getDecorView().getRootView().getWidth();
        int height = getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片

        temBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //找到当前页面的跟布局
        View view = getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = view.getDrawingCache();
       /* showImg.setImageBitmap(temBitmap);

        cutDialog.setView(view);
        Window window = cutDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        p.gravity = Gravity.CENTER;//设置弹出框位置
        window.setAttributes(p);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        cutDialog.show();
*/
        try {
            ContentResolver cr = DaBaoActivity.this.getContentResolver();
            Date bDate = new Date();

            insertImage(cr, temBitmap, "qraved", "a photo from app", bDate.getTime() + "");

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showLong(mContext, "截图失败，请检查是否打开App权限");

        }
        //对某些不更新相册的应用程序强制刷新
        Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File("/sdcard/image.jpg"));//固定写法
        intent2.setData(uri);
        DaBaoActivity.this.sendBroadcast(intent2);
        // cutDialog.dismiss();
    }


    /**
     * Insert an image and create a thumbnail for it.
     *
     * @param cr          The content resolver to use
     * @param source      The stream to use for the image
     * @param title       The name of the image
     * @param description The description of the image
     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
     * for any reason.
     */
    public String insertImage(ContentResolver cr, Bitmap source,
                              String title, String description, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.LATITUDE, 36);
        values.put(MediaStore.Images.Media.LONGITUDE, 120);
        values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "6666");


        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                Log.d("Alex", "Failed to create thumbnail, removing original进入的数据");
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 36, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                Bitmap microThumb = StoreThumbnail(cr, miniThumb, id, 50F, 50F,
                        MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                Log.d("Alex", "Failed to create thumbnail, removing original空的数据");
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            Log.i("Alex", "Failed to insert image异常的数据", e);
            // ToastUtil.showLong(mContext,"截图失败，请检查是否打开App权限");
            ToastUtil.showMyToast(6 * 1000, mContext, "截图失败，请检查是否打开App权限");
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }
        if (url != null) {

            //ToastUtil.showLong(mContext,"截图成功");
            Toast.makeText(getApplicationContext(), "账号密码已截图", Toast.LENGTH_LONG).show();
//            ToastUtil.showMyToast(3* 1000, mContext, "账号密码已截图");
            Log.d("Alex", "Failed to create thumbnail, removing original不等于空的数据");
            stringUrl = url.toString();
        }

        return stringUrl;
    }


    private static final Bitmap StoreThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width, float height,
            int kind) {
        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true);

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            Log.d("Alex", "Failed to create thumbnail, removing original最后的数据");
            OutputStream thumbOut = cr.openOutputStream(url);

            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            Log.d("Alex", "Failed to ----FileNotFoundException异常的数据");
            return null;
        } catch (IOException ex) {
            Log.d("Alex", "Failed to------ FileNotFoundException异常的数据");
            return null;
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11101)
            Tencent.onActivityResultData(requestCode, resultCode, data, BaseUiListener);
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    private static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {

        }

        return strMacAddr;
    }

    private String index;

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }


    @Subscriber(tag = "aaa")
    public void resultData(String code) {
//        mWebView.loadUrl("javascript:wxAndroid(\"" + code + "\")");
//        mWebView.loadUrl("javascript:clipboardAndroid(\"" + code + "\")");
        mWebView.loadUrl("javascript:wxAndroid(\"" + code + "\",\"" + WX_APP_ID + "\")");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    private void setVisibility(boolean isView) {
//       if ( a.a(21L,"type")){
//           com.cecil.okhttp.f.a.a();
//       }

        ivLoading.setVisibility(isView ? View.GONE : View.VISIBLE);
        webView1.setVisibility(isView ? View.VISIBLE : View.GONE);
        Glide.with(mContext).load(R.mipmap.loading).into(ivLoading);
    }

    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    public void addShortcut(String name, Uri uri) {

        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(DaBaoActivity.this,
                        R.mipmap.icon_logo));

        // 设置关联程序
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClass(DaBaoActivity.this, DaBaoActivity.class);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addShortcutIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        sendBroadcast(addShortcutIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void addShortCut(Context context) {
        ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);

        if (shortcutManager.isRequestPinShortcutSupported()) {
            Intent shortcutInfoIntent = new Intent(context, DaBaoActivity.class);
            shortcutInfoIntent.setAction(Intent.ACTION_VIEW); //action必须设置，不然报错

            ShortcutInfo info = new ShortcutInfo.Builder(context, "The only id")
                    .setIcon(Icon.createWithResource(context, R.mipmap.icon_logo))
                    .setShortLabel("Short Label")
                    .setIntent(shortcutInfoIntent)
                    .build();

            //当添加快捷方式的确认弹框弹出来时，将被回调
            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ShortcutsReciever.class), PendingIntent.FLAG_UPDATE_CURRENT);

            shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());
        }

    }

    private void showBottomDialog(final String url) {
        final Dialog bottomDialog = new Dialog(this, R.style.DialogTheme);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);
        contentView.findViewById(R.id.tv_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(url, 0);
                bottomDialog.dismiss();
            }
        });
        contentView.findViewById(R.id.tv_moments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(url, 1);
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQQ(url, 0);
                bottomDialog.dismiss();
            }
        });
        contentView.findViewById(R.id.tv_qq_moments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQQ(url, 1);
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });


        bottomDialog.setContentView(contentView);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomDialog.show();
    }

    private void shareQQ(String url, final int type) {
        String urlEncoded = Uri.decode(url);
        Log.e(TAG, "shouldOverrideUrlLoading: " + urlEncoded);

        String[] split = urlEncoded.split(",");
        final Bundle params = new Bundle();
        if (type == 0) {

            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, split[1]);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, split[2]);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, split[3]);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, split[4]);
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用222222");
            mTencent.shareToQQ(DaBaoActivity.this, params, BaseUiListener);
        } else {
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, split[1]);//必填
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, split[2]);//选填
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, split[3]);//必填
            ArrayList<String> imgUrlList = new ArrayList<>();
            imgUrlList.add(split[3]);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);// 图片地址
//            params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
            mTencent.shareToQzone(DaBaoActivity.this, params, BaseUiListener);
        }

    }

    private void share(String url, final int type) {
        String urlEncoded = Uri.decode(url);

        String[] split = urlEncoded.split(",");

        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = split[3];

//用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = split[1];
        msg.description = split[2];
//        Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_applog);
//        msg.thumbData = FileUtils.compressImage(thumbBmp);
        Glide.with(DaBaoActivity.this).load(split[4]).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                msg.thumbData = FileUtils.compressImage(resource);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis());
                req.message = msg;
                req.scene = type == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                Constants.wx_api.sendReq(req);
            }
        });
    }

    private void clipboardAndroid(Context context) {
        //系统剪贴板-获取:
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 返回数据
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            // 从数据集中获取（粘贴）第一条文本数据
            String text = clipData.getItemAt(0).getText().toString();
            Log.e(TAG, "clipboardAndroid: text" +text );
            if (text.length() < 5) return;
            String start = text.substring(0, 2);
            String end = text.substring(text.length() - 2, text.length());
            if (TextUtils.equals("$$", start) && TextUtils.equals("$$", end)) {
                Log.e(TAG, "clipboardAndroid: " + text);
                mWebView.loadUrl("javascript:clipboardAndroid(\"" + text + "\")");
            }
        }else {
            Log.e(TAG, "clipboardAndroid: 没有内容" );
        }


    }

    @OnClick(R.id.login)
    public void onViewClicked() {
//        //QQ第三方登录
//        Tencent mTencent = Tencent.createInstance(QQ_APP_ID, getApplicationContext());//1106782196       101441019     这里的“123123123”改为自己的Appid
//        mTencent.login(DaBaoActivity.this, "all", BaseUiListener);
//        startActivity(new Intent(this, TextActivity.class));
//        String a = null;
//        a.length();

//        clipboardAndroid(this);
    }

    private IUiListener BaseUiListener = new IUiListener() {

        @Override
        public void onComplete(Object o) {
            Log.e(TAG, "onComplete: " + o);

//            new AlertDialog.Builder(DaBaoActivity.this)
//                    .setMessage(o.toString())
//                    .show();


            String openidString = null;
            String strAccessToken = null;
            try {
                strAccessToken = ((JSONObject) o).getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mWebView.loadUrl("javascript:qqAndroid(\"" + QQ_APP_ID + "\",\"" + strAccessToken + "\")");
        }

        @Override
        public void onError(UiError e) {
            Log.e("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        goneSystemUi();

    }

    private static boolean isStatusbarVisible(Activity activity) {
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        boolean isStatusbarHide = ((uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN) == uiOptions);
        return !isStatusbarHide;
    }

    public static void hideStatusBar(Activity activity) {
        if (isStatusbarVisible(activity)) {
            int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    public void goneSystemUi() {
        //隐藏虚拟按键
        if (Build.VERSION.SDK_INT < 19) {
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
