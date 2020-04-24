package com.huimee.dabaoapp.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.huimee.dabaoapp.BuildConfig;
import com.huimee.dabaoapp.database.SpCache;
import com.huimee.dabaoapp.utils.HttpLoggingInterceptor;
import com.huimee.dabaoapp.utils.Timber;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.tinker.entry.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;


/**
 * Author Administrator
 * on 2016/6/29.
 * 全局的Application，用来存放一些全局的变量
 */
public class MyApplication extends Application {
    private static Context mContext;
    private static Handler mHandler;
    public static String mUserAgent;
    public static String WX_APP_ID ;
    public static String QQ_APP_ID ;
    private static long mMainThreadId;
    private List<Activity> mList = new LinkedList<Activity>();
    private HashMap<String, Activity> map = new HashMap<>();
    private ApplicationLike tinkerApplicationLike;
    private static MyApplication mApplication;
    private static HttpLoggingInterceptor.Logger mLogger;
    private Object mToken;

    public static MyApplication getInstance() {
        if (mApplication == null) {
            mApplication = new MyApplication();
        }
        return mApplication;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addHotelActivity(String name, Activity activity) {
        map.put(name, activity);
    }

    public void removeHotelActivity(String name) {
        map.remove(name);
    }

    public void removeHotelAllActivity() {

        try {
            for (Activity v : map.values()) {
                if (null != v) {
                    v.finish();
                }
            }
            map.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addActivitys(String name, Activity activity) {
        map.put(name, activity);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);*/

        //JPushInterface.setDebugMode(true);//设置开启日志，发布的时候记得关闭
        //JPushInterface.init(this);          //初始化极光推送

        mApplication = this;
        SpCache.init(this);//初始化spCache
        initTinkerPatch();
        // UMConfigure.init(this,"5ad5c5f38f4a9d7314000052","umeng",UMConfigure.DEVICE_TYPE_PHONE,"");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0//----5a12384aa40fa3551f0001d1
        //PlatformConfig.setWeixin("wxdd29219ca4d0fde1", "ab6e8d46821d32a8dec6491f9205b5b8");
        // SocializeConstants.SDK_VERSION
        WX_APP_ID = "wx4df863a2c1e83fc0";
        QQ_APP_ID = "101521879";
        mUserAgent = "_android_app_v1.7_syjplus";
        UMConfigure.init(this, "5d47db204ca357d6b5000220", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        UMConfigure.setLogEnabled(true);

        //douyin
//        WX_APP_ID = "wx47956eaf0b3d3e1c";
//        QQ_APP_ID = "101617720";
//        mUserAgent = "_android_app_v1.7_hmtlkt";
//        UMConfigure.init(this, "5ea14d69895cca8948000181", "Umeng_01", UMConfigure.DEVICE_TYPE_PHONE, null);
//        // 选用AUTO页面采集模式
//        UMConfigure.setLogEnabled(true);


        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        JPushInterface.setDebugMode(true);// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        /*--------------- 创建应用里面需要用到的一些共有的属性 ---------------*/
        // 1.上下文
        mContext = getApplicationContext();

        // 2.主线程handler
        mHandler = new Handler();

        // 3.主线程的id
        mMainThreadId = android.os.Process.myTid();
        /**
         * Tid: thread
         * Pid: process
         * Uid: user
         */

//        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
//                .connectTimeout(300, TimeUnit.SECONDS)
//                .readTimeout(300, TimeUnit.SECONDS)
//                .cookieJar(cookieJar)
//                //其他配置
//                .build();
//

        Timber.plant(new Timber.DebugTree());

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(mLogger);
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is 的数据----------************------------" + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void initTinkerPatch() {
        if (BuildConfig.TINKER_ENABLE) {
            tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
            TinkerPatch.init(
                    tinkerApplicationLike
            )
                    .reflectPatchLibrary()
                    .setPatchRollbackOnScreenOff(true)
                    .setPatchRestartOnSrceenOff(true)
                    .setFetchPatchIntervalByHours(3)
                    .setFetchDynamicConfigIntervalByHours(3);

            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    static {
        mLogger = new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {
                Timber.i(message);
            }
        };
    }

    /**
     * 初始化回调
     * 在回调接口的方法里，第三方开发者实现获取token的异步请求
     *
     * @param listener 回调接口
     */
    /*public void setTokenListener(Token.TokenListener listener) {
        TokenManager.getInstance().setTokenListener(listener);
    }*/


    private Map<String, String> mProtocolCacheMap = new HashMap<>();

    public Map<String, String> getProtocolCacheMap() {
        return mProtocolCacheMap;
    }

    /**
     * 得到上下文
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * 得到主线程的handler
     */
    public static Handler getHandler() {
        return mHandler;
    }

    /**
     * 得到主线程的线程id
     */
    public static long getMainThreadId() {
        return mMainThreadId;
    }
}
