package com.huimee.dabaoapp.install;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Author Administrator
 * on 2016/9/30.
 * 安装下载接收器
 */

public class InstallReceiver extends BroadcastReceiver {

    //安装下载接收器
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            //            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            installApk(context, -1);
        }
    }

    //安装Apk
    private void installApk(Context context, long downloadApkId) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        String filePath = "/sdcard/download/download.apk";
        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

        //         获取存储ID
        //        long id = (long) SPUtils.get(context, CommonCons.DOWNLOAD_APK_ID_PREFS,-1l);
        //
        //        if (downloadApkId == id) {
        //            DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //            //Intent install = new Intent(Intent.ACTION_VIEW);
        //            Intent install=new Intent(Intent.ACTION_VIEW);
        //            Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        //
        //            LogUtil.e(TAG,"-----> "+downloadFileUri.toString() +"   path " + downloadFileUri.getPath());
        //
        //             if (downloadFileUri != null) {
        //                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
        //                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //                try {
        //                    context.startActivity(install);
        //                }catch (Exception e){
        //                    LogUtil.e(TAG,"-----> 安装异常 " + e.getMessage());
        //                }
        //
        //
        //            } else {
        //                LogUtil.e(TAG, "------------下载失败");
        //            }
        //        }
    }
}
