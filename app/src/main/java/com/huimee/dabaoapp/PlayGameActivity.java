package com.huimee.dabaoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.google.gson.Gson;
import com.huimee.dabaoapp.base.MyBaseActivity;
import com.huimee.dabaoapp.bean.WxPayBean;
import com.huimee.dabaoapp.ui.view.X5WebView;
import com.huimee.dabaoapp.utils.ToastUtil;
import com.huimee.dabaoapp.utils.WXPayManager;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Date;

import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by XY on 2018/3/29.
 */

public class PlayGameActivity extends MyBaseActivity {
    public static final String TAG = "PlayGameActivity";
    @InjectView(R.id.webview_play_game)
    BridgeWebView webviewPlayGame;

    @InjectView(R.id.iv_xfq)
    ImageView ivXfq;

    int screenWidth, screenHeight;
    int lastX, lastY;
    //设置缓存webview的路径
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    @InjectView(R.id.webView1)
    FrameLayout webView1;
    @InjectView(R.id.iv_loading)
    ImageView ivLoading;
    private String url, gameId;
    private ProgressDialog dialog;


    private Date aDate, bDate;
    private int num;
    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    private X5WebView mWebView;
    private ViewGroup mViewParent;

    @Override
    protected void findById() {
        EventBus.getDefault().register(this);
        startActivitys(DaBaoActivity.class);
        finish();
    }

    @Override
    protected void init() {

        initHelp();
       // initView2();
        // initView();
        // autoPlay();
    }

    @Override
    protected void loadData() {
        aDate = new Date();//开始时间

    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_play_game;
    }

    /*************************** 使用帮助 ****************************/
    /*
     * @ViewInject(R.id.ivXfq) private TextView ivXfq;
	 */
    private void initHelp() {
        Display dis = getWindowManager().getDefaultDisplay();
        screenWidth = dis.getWidth();
        screenHeight = dis.getHeight();

        ivXfq.setOnTouchListener(new View.OnTouchListener() {

            int dx = 0;
            int dy = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_CANCEL:
                        Log.i("TAG", "MotionEvent返回的数据---被点击了---ACTION_CANCEL---事件被拦截");
                        // 事件被拦截
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        Log.i("TAG", "MotionEvent返回的数据---被点击了---ACTION_OUTSIDE---超出区域");
                        // 超出区域
                        break;

                    case MotionEvent.ACTION_DOWN:
                        Log.i("TAG", "MotionEvent返回的数据---被点击了---ACTION_DOWN---手指按下");
                        dx = 0;
                        dy = 0;
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.i("TAG",  dx +"MotionEvent返回的数据---被点击了---ACTION_MOVE---手指移动------" + dy);
                       /* dx = (int) event.getRawX() - lastX;
                        dy = (int) event.getRawY() - lastY;

                        int top = v.getTop() + dy;

                        int left = v.getLeft() + dx;

                        if (top <= 0) {
                            top = 0;
                        }
                        if (top >= screenHeight - ivXfq.getHeight()) {
                            top = screenHeight - ivXfq.getHeight();
                        }
                        if (left >= screenWidth - ivXfq.getWidth()) {
                            left = screenWidth - ivXfq.getWidth();
                        }

                        if (left <= 0) {
                            left = 0;
                        }

                        v.layout(left, top, left + ivXfq.getWidth(), top
                                + ivXfq.getHeight());
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();*/
//移动时触发
                         dx =(int)event.getRawX() - lastX;//计算x和y的变化量
                         dy =(int)event.getRawY() - lastY;

                        int left = v.getLeft() + dx;//计算实时的坐标
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        //限制坐标，不能超过屏幕的上下左右
                        if(left < 0){
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if(right > screenWidth){
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if(top < 0){
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if(bottom > screenHeight){
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();


                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("TAG", dx + "MotionEvent返回的数据---被点击了---ACTION_UP---手指抬起" + dy);
                        if ((dx <= 3 && dx >= -3) && (dy <= 3 && dy >= -3)) {

                            startActivitys(DaBaoActivity.class);
                        } else {
                            // 每次移动都要设置其layout，不然由于父布局可能嵌套listview，当父布局发生改变冲毁（如下拉刷新时）则移动的view会回到原来的位置
                            RelativeLayout.LayoutParams lpFeedback = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lpFeedback.leftMargin = v.getLeft();
                            lpFeedback.topMargin = v.getTop();
                            lpFeedback.setMargins(v.getLeft(), v.getTop(), 0, 0);
                            v.setLayoutParams(lpFeedback);
                        }
                        break;

                }

                return true;
            }

        });
    }


    private void initView2() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("http://www.sooyooj.com/play.html?id=15&uid=15624&token=mG3chboySSdpbx4sxSGhIhWHccrENiqUT2qLWuFoNhU3tMv7FKAymn3OYXCGF7MHVmVtPcJd&a=PVRo8WwDkoAnx41hpFC0YzIX834ueabT");
            gameId = bundle.getString("gameId");
        }
url="http://www.sooyooj.com/play.html?id=15&uid=15624&token=mG3chboySSdpbx4sxSGhIhWHccrENiqUT2qLWuFoNhU3tMv7FKAymn3OYXCGF7MHVmVtPcJd&a=PVRo8WwDkoAnx41hpFC0YzIX834ueabT";
        mWebView = new X5WebView(this, null);

        webView1.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));


        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                   view.loadUrl(url);
                //return false;

                // 判断url链接中是否含有某个字段，如果有就执行指定的跳转（不执行跳转url链接），如果没有就加载url链接
                if (url.contains("wxpay")) {
                    getWxPay(url);
                    return true;
                } else if (url.contains("alipay")) {
                    Log.i("TAG", "url-----url---返回的数据" + url);
                    // webviewPlayGame.reload();
                    //webviewPlayGame.goBack();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                    // Glide.with(context).load(R.mipmap.image).into(mImageView);
                    return true;
                } else {
                    return false;
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                view.getUrl();
                // mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
                //mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?


            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            // /////////////////////////////////////////////////////////
            //


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
            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                TbsLog.d("", "url: " + arg0);
                new AlertDialog.Builder(PlayGameActivity.this)
                        .setTitle("allow to download？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        ToastUtil.showLong(mContext, "fake message: i'll download...");
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        ToastUtil.showLong(mContext, "fake message: refuse download...");
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // TODO Auto-generated method stub
                                        ToastUtil.showLong(mContext, "fake message: refuse download...");
                                    }
                                }).show();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                view.loadUrl("javascript:$('a[id=glTouch]').remove()");//glTouch1
                if(newProgress<num){
                    aDate = new Date();
                }
                num=newProgress;
                bDate = new Date();

                long interval = (bDate.getTime() - aDate.getTime()) / 1000;

                if (interval > 5  && interval <=35) {
                    ToastUtil.showLong(mContext, "努力加载中。。。请耐心等待");
                }

                if (interval >35) {
                    ToastUtil.showLong(mContext, "请重新进入游戏或者切换网络后再试！");
                    finish();
                }
                if (newProgress > 80) {
/*
                    ivLoading.setVisibility(View.GONE);
                    webView1.setVisibility(View.VISIBLE);*/
                } else {
                 /*   ivLoading.setVisibility(View.VISIBLE);
                    webView1.setVisibility(View.GONE);
                    Glide.with(mContext).load(R.mipmap.loading).into(ivLoading);*/
                }

            }
        });

        WebSettings webSetting = mWebView.getSettings();
        // 获取到UserAgentString
        String userAgent = webSetting.getUserAgentString();
        // 打印结果
        Log.i("TAG", "User Agent:返回的数据" + userAgent);
        webSetting.setUserAgentString(userAgent + "_android_app_wap");//_android_app
        Log.i("TAG", "User Agent:222返回的数据" + webSetting.getUserAgentString());

        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        Log.d(TAG, "xiaodai----url" + url);
        mWebView.loadUrl(url);
        //  mWebView.loadUrl("http://pt1.xd-game.com/sooyooj/login?uid=28957&ext=EYtDgWqxRpYLxYFAP6EaJTtKLnXmRj0H1uheBsVz6met0OKUSjRqVawZSTgMvLDJbaFNxYAQh56LU31gNsc6JzeCRW0osXF7mruY3JQCwoT7yGyNrAJZbULU08HFpLIO&time=1525936488&nonce=t1E65yIE4B&sign=7bef21bffada89659d4ca82dc6ee023e");

        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();


    }

/*    private void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
            gameId = bundle.getString("gameId");
        }


        WebSettings webSettings = webviewPlayGame.getSettings();
        // 获取到UserAgentString
        String userAgent = webSettings.getUserAgentString();
        // 打印结果
        Log.i("TAG", "User Agent:返回的数据" + userAgent);
        webSettings.setUserAgentString(userAgent + "_android_app");

        Log.i("TAG", "User Agent:222返回的数据" + webSettings.getUserAgentString());


        webSettings.setJavaScriptEnabled(true);//设置支持javaScript
        // webviewLookMovie.requestFocusFromTouch();

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//关闭WebView中缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭WebView中缓存
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setUseWideViewPort(true); // 关键点
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);//设置适应Html5 //重点是这个设置//开启 DOM storage API 功能


*//*        //设置渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
//设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath);
//设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
//开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
//设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        // 支持多窗口
        webSettings.setSupportMultipleWindows(true);

        if (isNetworkAvailable(getApplicationContext())) {
            //有网络连接，设置默认缓存模式
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //无网络连接，设置本地缓存模式
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }*//*

        webviewPlayGame.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
           *//*     if(newProgress == 100){
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }else{
                    if(dialog == null){
                        dialog = new ProgressDialog(mContext);
                        dialog.setTitle("正在加载中.....");
                        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        dialog.setProgress(newProgress);
                        dialog.show();
                    }else{
                        dialog.setProgress(newProgress);
                    }
                }*//*
                if (newProgress == 100) {
                    getProgressDialog("玩命加载中.....").dismiss();
                } else {
                    getProgressDialog("玩命加载中.....").show();
                }

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        Log.d(TAG, "url--url" + url);
        webviewPlayGame.loadUrl(url);

        webviewPlayGame.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                   view.loadUrl(url);
                //return false;

                // 判断url链接中是否含有某个字段，如果有就执行指定的跳转（不执行跳转url链接），如果没有就加载url链接
                if (url.contains("wxpay")) {
                    getWxPay(url);
                    return true;
                } else if (url.contains("type=alipay")) {
                    // webviewPlayGame.reload();
                    //webviewPlayGame.goBack();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                view.getUrl();

                autoPlay();
             *//*   view.loadUrl("javascript:(function() { " +
                        "var videos = document.getElementsByTagName('audio');" +
                        " for(var i=0;i<videos.length;i++){videos[i].play();}})()");
                view.loadUrl("javascript:(function() { " +
                        "var videos = document.getElementsByTagName('video');" +
                        " for(var i=0;i<videos.length;i++){videos[i].play();}})()");*//*
                // view.loadUrl("javascript:$('a[id=glTouch]').remove()");

                Log.d(TAG, "dashabi返回的数据url----" + url);
                Log.d(TAG, "dashabi返回的数据getUrl----" + view.getUrl());
                Log.d(TAG, "dashabi返回的数据getOriginalUrl----" + view.getOriginalUrl());

            }

        });
//网页加载完成


    }*/

    /**
     * 使视频或音频自动播放,默认播放第一条
     */
    public void autoPlay() {
        //音频自动播放js方法
        String audioJs = "javascript: var v = document.getElementsByTagName('audio'); v[0].play();";
        webviewPlayGame.loadUrl(audioJs);
        //视频自动播放js方法
        String videoJs = "javascript: var v = document.getElementsByTagName('video'); v[0].play();";
        webviewPlayGame.loadUrl(videoJs);
    }

    private void wxPay(String response) {
        //微信支付获取参数
//        WXPayManager.init(mContext, Constants.WX_APP_ID);
        WXPayManager.getInstance().doPay(response, new WXPayManager.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "WXPayManager返回的数据url----支付成功");
                ToastUtil.showLong(mContext, "支付成功");
            }

            @Override
            public void onError(int error_code) {
                Log.d(TAG, "WXPayManager返回的数据url----支付失败，请重试");
                ToastUtil.showLong(mContext, "支付失败，请重试");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "WXPayManager返回的数据url----支付取消");
                ToastUtil.showLong(mContext, "支付取消");
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
/*        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webviewPlayGame.canGoBack()) {  //表示按返回键
                    webviewPlayGame.goBack();   //后退
                    //webview.goForward();//前进
                    return true;    //已处理
                }
            }

        }*/
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webviewPlayGame.canGoBack()) {
            webviewPlayGame.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 检测当前网络可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }





    @Subscriber(tag = PlayGameActivity.TAG)
    private void out(String string) {
        finish();

    }

    @Subscriber(tag = "reloadWebview")
    private void reload(String string) {
        mWebView.reload();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //webviewPlayGame.removeAllViews();
        //webviewPlayGame.destroy();//防止WebView加载内存泄漏

        if (mWebView != null) {
            mWebView.destroy();//防止WebView加载内存泄漏
        }

    }


    /**
     * 获取微信支付参数
     */
    private void getWxPay(String mUrl) {


    }


}
