package com.huimee.dabaoapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ZHY
 *         address  https://github.com/hongyangAndroid
 *         date 2015-12-18 19:17
 *         description SP封装 （下一步改善，取出SpCaceh不行相关的功能，比如存储用户信息，注意职责单一）
 *         vsersion 1.0
 */
public class SpCache {
    public static final String TOKEN = "token";


    //登录类型 1：   游侠客户登录    2：  等三方登录
    public static final String LOGINTYPE = "loginType";
    public static final String THIRDLOGINTYPE = "thirdLoginType";
    public static final String SEARCH_STYLE = "search_style";
    public static final String APP_IS_NEWCOME = "isNewcome";

    public static final String LOGGEDUSERNAME = "loggedUsername";

    public static final String LOGINSTATE = "login_state";//用户是否登陆
    public static final String USERID = "uid";//用户id
    public static final String USER_ACCOUNT = "user_account";//登陆账号
    public static final String USER_PHOTO = "user_photo";//用户头像
    public static final String PASSWORD = "password";//用户密码
    public static final String USER_PHONE = "user_phone";//用户电话
    public static final String USER_NAME = "user_name";//用户名字

    public static final String BIND_USER_ID = "bind_user_id";//用户是否绑定手机
    public static final String IS_NEW_PASSWORD = "is_new_password";//是否使用新密码
    public static final String LOGIN_SUCCESS = "login_success";//登录成功



    public static final String QQ_OPEN_ID = "qq_openId";
    public static final String SINA_OPEN_ID = "sina_openId";
    public static final String WEIXIN_OPEN_ID = "weixin_openId";


    public static final String REFRESH_TIME = "refreshTime";//保存刷新时间

    public static final String OF_MONTH = "ofMonth";//订单列表保存刷新时间的月份
    public static final String OF_DATE = "ofDate";//订单列表保存刷新时间的天
    public static final String OF_HOUR = "ofHour";//订单列表保存刷新时间的小时
    public static final String OF_MINUTE = "ofMinute";//订单列表保存刷新时间的分钟


    public static final String STATE_Two = "state_two";//APP安装完成后初次运行时调用，----打包专用
    public static final String SEND_MAC_TWO = "send_mac_two";//是否是第二次注册登录成功
    public static final String IF_TBS = "if_tbs";//是否是第二次

    public static final String URL_Two25 = "url_two25";//跳转后的链接
    public static final String STATE_Two25 = "state_two25";//是否是第一次进入：1是第一次，


    public static final String QD_ID = "qd_id";//本地的渠道ID
    public static final String REGISTER_TWO="register_two";//第一次注册
    /**
     * 认证信息界面的信息
     */


    private static final String TAG = SpCache.class.getSimpleName();
    private static SpCache INSTANCE;
    private ConcurrentMap<String, SoftReference<Object>> mCache;
    private String mPrefFileName = "yxlx_spcache";
    private Context mContext;

    private SpCache(Context context, String prefFileName) {
        mContext = context.getApplicationContext();
        mCache = new ConcurrentHashMap<>();
        initDatas(prefFileName);

    }

    private void initDatas(String prefFileName) {
        if (null != prefFileName && prefFileName.trim().length() > 0) {
            mPrefFileName = prefFileName;
        } else {
            Log.d(TAG, "prefFileName is invalid , we will use default value ");
        }

    }

    public static SpCache init(Context context, String prefFileName) {
        if (INSTANCE == null) {
            synchronized (SpCache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SpCache(context, prefFileName);
                }
            }
        }
        return INSTANCE;
    }

    public static SpCache init(Context context) {
        return init(context, null);
    }

    private static SpCache getInstance() {
        if (INSTANCE == null)
            throw new NullPointerException("you show invoke SpCache.init() before you use it ");

        return INSTANCE;
    }


    //put
    public static SpCache putInt(String key, int val) {
        return getInstance().put(key, val);
    }

    public static SpCache putLong(String key, long val) {
        return getInstance().put(key, val);
    }

    public static SpCache putString(String key, String val) {
        return getInstance().put(key, val);
    }

    public static SpCache putBoolean(String key, boolean val) {
        return getInstance().put(key, val);
    }

    public static SpCache putFloat(String key, float val) {
        return getInstance().put(key, val);
    }


    //get
    public static int getInt(String key, int defaultVal) {
        return (int) (getInstance().get(key, defaultVal));
    }

    public static long getLong(String key, long defaultVal) {
        return (long) (getInstance().get(key, defaultVal));
    }

    public static String getString(String key, String defaultVal) {
        return (String) (getInstance().get(key, defaultVal));
    }

    public static boolean getBoolean(String key, boolean defaultVal) {
        return (boolean) (getInstance().get(key, defaultVal));
    }

    public static float getFloat(String key, float defaultVal) {
        return (float) (getInstance().get(key, defaultVal));
    }

    //contains
    public boolean contains(String key) {
        return mCache.get(key).get() != null ? true : getSharedPreferences().contains(key);
    }

    //remove
    public static SpCache remove(String key) {
        return INSTANCE._remove(key);
    }

    private SpCache _remove(String key) {
        mCache.remove(key);
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
        return INSTANCE;
    }

    //clear
    public static SpCache clear() {
        return INSTANCE._clear();
    }

    private SpCache _clear() {
        mCache.clear();
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
        return INSTANCE;
    }

    private <T> SpCache put(String key, T t) {
        mCache.put(key, new SoftReference<Object>(t));
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (t instanceof String) {
            editor.putString(key, (String) t);
        } else if (t instanceof Integer) {
            editor.putInt(key, (Integer) t);
        } else if (t instanceof Boolean) {
            editor.putBoolean(key, (Boolean) t);
        } else if (t instanceof Float) {
            editor.putFloat(key, (Float) t);
        } else if (t instanceof Long) {
            editor.putLong(key, (Long) t);
        } else {
            Log.d(TAG, "you may be put a invalid object :" + t);
            editor.putString(key, t.toString());
        }

        SharedPreferencesCompat.apply(editor);
        return INSTANCE;
    }


    private Object readDisk(String key, Object defaultObject) {
        Log.e("TAG", "readDisk");
        SharedPreferences sp = getSharedPreferences();

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        Log.e(TAG, "you can not read object , which class is " + defaultObject.getClass().getSimpleName());
        return null;

    }

    private Object get(String key, Object defaultVal) {
        SoftReference reference = mCache.get(key);
        Object val = null;
        if (null == reference || null == reference.get()) {
            val = readDisk(key, defaultVal);
            mCache.put(key, new SoftReference<Object>(val));
        }
        val = mCache.get(key).get();
        return val;
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(final SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }


    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(mPrefFileName, Context.MODE_PRIVATE);
    }


    public static String getToken() {
        return getString(TOKEN, "");
    }


    public static String getUserId() {
        return getString(USERID, "");
    }
    public static boolean getSearchStyle() {
        return getBoolean(SEARCH_STYLE, true);
    }

    public static boolean getAppIsNewCome() {
        return getBoolean(APP_IS_NEWCOME, true);
    }

    public static String getRefreshTime() {
        return getString(REFRESH_TIME, "");
    }

    public static String getOfMonth() {
        return getString(OF_MONTH, "");
    }

    public static String getOfDate() {
        return getString(OF_DATE, "");
    }

    public static String getOfHour() {
        return getString(OF_HOUR, "");
    }

    public static String getOfMinute() {
        return getString(OF_MINUTE, "");
    }

    public static String getUserPhoto() {
        return getString(USER_PHOTO, "");
    }
    public static String getUserAccount() {
        return getString(USER_ACCOUNT, "");
    }
    public static String getUserName() {
        return getString(USER_NAME, "");
    }


    public static String getUrlTwo25() {
        return getString(URL_Two25, "");
    }
    public static String getStateTwo25() {
        return getString(STATE_Two25, "");
    }
    public static String getStateTwo() {
        return getString(STATE_Two, "");
    }
    public static String getQdId() {
        return getString(QD_ID, "");
    }

    public static String getSendMacTwo() {
        return getString(SEND_MAC_TWO, "");
    }
    public static String getIfTbs() {
        return getString(IF_TBS, "");
    }

    public static String getRegisterTwo() {
        return getString(REGISTER_TWO, "");
    }

}
