package com.huimee.dabaoapp.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Author Administrator
 * on 2016/6/27.
 * 所有Activity的基类，提供一些公共的方法
 */
public class BaseActivity extends AppCompatActivity {




    /**
     * 直接跳转页面
     *
     * @param clazz
     */
    protected void startActivitys(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * 跳转需要传递参数的页面
     *
     * @param clazz
     * @param bundle
     */
    protected void startActivitys(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 跳转到下一个页面，希望下一个页面需要有返回值
     *
     * @param clazz
     * @param bundle
     */
    protected void startActivityForResults(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0x1000);
    }
    /**
     * 跳转到下一个页面，希望下一个页面需要有返回值
     * @param clazz
     */
    protected void startActivityForResults(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, 0x1000);
    }
}
