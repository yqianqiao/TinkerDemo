package com.huimee.dabaoapp.config;


import com.huimee.dabaoapp.utils.LogUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * Author Administrator
 * on 2016/6/30.
 * 接口文档的接口数据
 */
public class Constants {

    public static final String PROJECT_NAME = "搜游记";
    public static final String APK_NAME = "syj.apk";


    //code # 0 : 请求失败 , 1 : 请求成功 , 2 : 未登录 , 3 : 没有权限

    // message # 消息提示

    // response # 响应主体

    /**
     * 生成秘钥需要的参数
     */

//    public static final String WX_APP_ID = "wxdd29219ca4d0fde1";


    public static IWXAPI wx_api; //全局的微信api对象
    /**
     * LEVEL_ALL:打开应用程序里面所有输入的日志 7
     * LEVEL_OFF:关闭应用程序里面所有输入的日志 0
     */
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;


    /**
     * 基础网址
     */
    public static final String BASEURL = "http://api.sooyooj.com";

    /**
     * 检查App版本更新的网址
     */
    public static final String VERSION_ACTIVE = "/index/android/version/active";

    /**
     * APP安装完成后初次运行时调用此接口
     */
    public static final String INSTALL_COUNT = "/index/app/install/count";

    /**
     * 获取安卓最新版本的下载连接的接口，目前返回值暂为空
     */
    public static final String VERSION_NEWLY = "/index/android/version/newly";

    /**
     * 发送手机登录验证码
     */
    public static final String PHONE_CODE = "/index/login/phone/code";
    /**
     * 登录
     */
    public static final String LOGIN = "/index/login/login";


    /**
     * 参数
     * version 版本号，当前可用版本号v1.0
     * 返回值
     * value 为1时表示此版本可用，为0时表示此版本不可用，需要更新
     * 接口检测安卓独立安装包是否可用
     */
    public static final String EXTRA_VERSION_ACTIVE = "/index/android/extra/version/active";


    /**
     * 接口新增 extra_android 返回值，表示安卓独立安装包的最新下载地址
     */
    public static final String DOWNLLOAD_LINK = "/index/app/download/link";


    /**
     * 跟新
     */
    public static final String UP_APK = "/index/android/version2";


}
