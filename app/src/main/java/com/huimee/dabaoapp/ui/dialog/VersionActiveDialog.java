package com.huimee.dabaoapp.ui.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyco.animation.ZoomEnter.ZoomInEnter;
import com.flyco.dialog.widget.base.BaseDialog;
import com.huimee.dabaoapp.R;
import com.huimee.dabaoapp.bean.DownlloadLinkBean;
import com.huimee.dabaoapp.bean.UpAPK;
import com.huimee.dabaoapp.config.Constants;
import com.huimee.dabaoapp.install.UpdateAppUtils;
import com.huimee.dabaoapp.utils.ToastUtil;

/**
 * Created by XY on 2018/5/14.
 */

public class VersionActiveDialog extends BaseDialog {
    public static final String TAG = "VersionActiveDialog";
    final public static int WRITE_EXTERNAL_STORAGE_RESULT_CODE = 123;
    private Context mContext;
    private TextView tvCancelVersionActive;
    private TextView tvVersionActive;
    private TextView tvVersion;
    private String versionNewlyBean;
    private UpAPK.ResponseBean upapk;

    private Activity activity;
    private String mType;

    public VersionActiveDialog(Context context, String versionNewlyBean, Activity activity, String type) {
        super(context);
        this.versionNewlyBean = versionNewlyBean;
        this.activity = activity;
        mContext = context;
        mType = type;
    }

    public VersionActiveDialog(Context context, UpAPK.ResponseBean upapk, Activity activity, String type) {
        super(context);
        this.upapk = upapk;
        this.activity = activity;
        mContext = context;
        mType = type;
    }

    @Override
    public View onCreateView() {
        widthScale(0.7f);
        showAnim(new ZoomInEnter());
        View dialog = View.inflate(mContext, R.layout.dialog_version_active, null);
        tvCancelVersionActive = (TextView) dialog.findViewById(R.id.tv_cancel_version_active);
        tvVersionActive = (TextView) dialog.findViewById(R.id.tv_version_active);
        tvVersion = (TextView) dialog.findViewById(R.id.tv_version);
        tvVersion.setText("有新版本需要更新");
        return dialog;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            Log.d(TAG, "getVersion: Dialog客户端的版本号是的数据    --    " + version);
            return "v" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "v1.0";
        }
    }

    @Override
    public void setUiBeforShow() {
        tvCancelVersionActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvVersionActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
               /* ToastUtil.showLong(mContext, "正在清理缓存...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DataCleanManager.clearAllCache(mContext);
                        EventBus.getDefault().post("MineSetUpActivity", MineSetUpActivity.TAG);
                    }
                }, 2000);*/
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_RESULT_CODE);
                        return;
                    } else {
                        //直接调用更新方法
                        UpdateAppUtils.downloadApk(mContext, upapk, Constants.PROJECT_NAME, Constants.APK_NAME, mType);
                        ToastUtil.showLong(mContext, "下载中...");
                    }
                } else {
                    //直接调用更新方法
                    ToastUtil.showLong(mContext, "下载中...");
                    UpdateAppUtils.downloadApk(mContext, upapk, Constants.PROJECT_NAME, Constants.APK_NAME, mType);
                }


            }
        });
    }

}
