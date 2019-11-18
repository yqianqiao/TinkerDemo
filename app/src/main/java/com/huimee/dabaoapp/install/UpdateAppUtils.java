package com.huimee.dabaoapp.install;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.huimee.dabaoapp.bean.DownlloadLinkBean;
import com.huimee.dabaoapp.bean.UpAPK;
import com.huimee.dabaoapp.bean.VersionNewlyBean;
import com.huimee.dabaoapp.utils.StringUtil;
import com.huimee.dabaoapp.utils.ToastUtil;
import com.tencent.connect.common.Constants;

import java.io.File;


/**
 * Author Administrator
 * on 2016/9/30.
 * 跟新管理器
 */

public class UpdateAppUtils {

    @SuppressWarnings("unused")
    private static final String TAG = "UpdateAppUtils";
    private static String VEWSION;

    private static String DownLoadUrl;

    /**
     * 检查更新
     */
    @SuppressWarnings("unused")
    public static void checkUpdate(String appCode, String curVersion, UpdateCallback updateCallback) {
        //        UpdateService updateService =
        //                ServiceFactory.createServiceFrom(UpdateService.class, UpdateService.ENDPOINT);
        //
        //        updateService.getUpdateInfo(appCode, curVersion)
        //                .subscribeOn(Schedulers.newThread())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(updateInfo -> onNext(updateInfo, updateCallback),
        //                        throwable -> onError(throwable, updateCallback));
    }

    // 显示信息
    private static void onNext(VersionNewlyBean versionNewlyBean, UpdateCallback updateCallback) {
        //        Log.e(TAG, "返回数据: " + updateInfo.toString());
        //        if (updateInfo.error_code != 0 || updateInfo.data == null ||
        //                updateInfo.data.appURL == null) {
        //            updateCallback.onError(); // 失败
        //        } else {
        //            updateCallback.onSuccess(updateInfo);
        //        }
    }

    // 错误信息
    private static void onError(Throwable throwable, UpdateCallback updateCallback) {
        //        updateCallback.onError();
    }

    /**
     * 下载Apk, 并设置Apk地址,
     * 默认位置: /storage/sdcard0/Download
     *
     * @param context          上下文
     * @param infoName         通知名称
     * @param storeApk         存储的Apk
     */
    @SuppressWarnings("unused")
    public static void downloadApk(
            Context context, UpAPK.ResponseBean upapk,
            String infoName, String storeApk, String type) {
        if (!isDownloadManagerAvailable()) {
            return;
        }

        //        String description = updateInfo.data.description;
        //        String appUrl = updateInfo.data.appURL;

        String appUrl = upapk.getAndroidextralink();
        // appUrl="http://www.youxia.com/app/app-release_187_jiagu_sign.apk";
        if (appUrl == null || appUrl.isEmpty()) {
            //LogUtil.e(TAG, "----->请填写App下载地址");
            Log.d(TAG, "downloadApk: 请填写下载地址");
            return;
        }

        appUrl = appUrl.trim(); // 去掉首尾空格999

        /*if (!appUrl.startsWith("http")) {
            appUrl = "http://" + appUrl; // 添加Http信息
        }*/

        //LogUtil.e(TAG, "--------> appUrl: " + appUrl);
        Log.d(TAG, "downloadApk: 下载地址返回的数据  --  " + appUrl);
//appUrl="http://api.sooyooj.com/index/app/download?t=android";
        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(appUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        request.setTitle(infoName);
        request.setDescription("版本升级");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        //sdcard目录下的download文件夹

        DownLoadUrl = StringUtil.getRandomString2(12);
        request.setDestinationInExternalPublicDir("/download", DownLoadUrl + ".apk");

        Context appContext = context.getApplicationContext();
        DownloadManager manager = (DownloadManager)
                appContext.getSystemService(Context.DOWNLOAD_SERVICE);

        // 存储下载Key
        //        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
        //        sp.edit().putLong(PrefsConsts.DOWNLOAD_APK_ID_PREFS, manager.enqueue(request)).apply();

        //        SPUtils.put(appContext, CommonCons.DOWNLOAD_APK_ID_PREFS,manager.enqueue(request));
        //manager.enqueue(request);


        try {
            long mReference = manager.enqueue(request);
         /*
        下载管理器中有很多下载项，怎么知道一个资源已经下载过，避免重复下载呢？
        我的项目中的需求就是apk更新下载，用户点击更新确定按钮，第一次是直接下载，
        后面如果用户连续点击更新确定按钮，就不要重复下载了。
        可以看出来查询和操作数据库查询一样的
         */
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mReference);
            Cursor cursor = manager.query(query);
            if (!cursor.moveToFirst()) {// 没有记录

            } else {
                //有记录
                //ToastUtil.showToast("已经下载");
            }

            //注册广播接收器
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(receiver, filter);
        } catch (Exception e) {
            //ToastUtil.showToast("请检查是否开启系统存储权限！");
            ToastUtil.showLong(context, "请检查是否开启系统存储权限");
            Log.d(TAG, "downloadApk: 请检查是否开启系统存储权限");
            e.printStackTrace();
        }

    }

    /**
     * 广播接受器, 下载完成监听器
     */
    public static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //下载完成了
                //获取当前完成任务的ID
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                Toast.makeText(context, "下载完成了", Toast.LENGTH_SHORT).show();

                //自动安装应用
                openFile(context);

            }

            if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                //广播被点击了
                Toast.makeText(context, "广播被点击了", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * apk自动安装
     *
     * @param context
     * @param
     */
    public static void openFile(Context context) {
/*        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Log.d(TAG, "downloadApk: 下载地址  --  ");
        Uri uri = Uri.fromFile(new File("/sdcard/Download/" + DownLoadUrl + "_" + ".apk")); //这里是APK路径
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);*/
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "com.huimee.dabaoapp"即是在清单文件中配置的authorities

            data = FileProvider.getUriForFile(context, getHostAppId(context), new File("/sdcard/Download/" + DownLoadUrl + ".apk"));
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(new File("/sdcard/Download/" + DownLoadUrl + ".apk"));
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    // 最小版本号大于9
    private static boolean isDownloadManagerAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    // 错误回调
    public interface UpdateCallback {
        void onSuccess(VersionNewlyBean versionNewlyBean);

        void onError();
    }

    public static String getHostAppId(Context appContext) throws IllegalArgumentException {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo == null) {
                throw new IllegalArgumentException(" get application info = null, has no meta data! ");
            }
            return applicationInfo.packageName+".provider";
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(" get application info error! ", e);
        }
    }
}
