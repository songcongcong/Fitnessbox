package com.yizutiyu.test.fitnessbox;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yizutiyu.test.fitnessbox.utils.AppUtils;

/**
 * BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        // 沉浸式
        AppUtils.setTranslucentStatus(this);
        setView();
        setData();
    }

    /**
     * 绑定布局
     * @return int
     */
    protected abstract int setLayout();

    /**
     * 初始化组件
     */
    protected abstract void setView();

    /**
     * 设置数据等逻辑代码
     */
    protected abstract void setData();

    /**
     * 简化findViewById()
     *
     * @param resId resId
     * @param <T> <T>
     * @return  view
     */
    protected <T extends View> T fvbi(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * Intent跳转
     *
     * @param context context
     * @param clazz clazz
     */
    protected void startActivity(Context context, Class<? extends BaseActivity> clazz) {
        startActivity(context, clazz, null);
    }

    /**
     * Intent带值跳转
     *
     * @param context context
     * @param clazz clazz
     * @param bundle bundle
     */
    protected void startActivity(Context context, Class<? extends BaseActivity> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * 带请求码跳转
     * @param targetClass targetClass
     * @param requestCode requestCode
     */
    protected void startActivity(Class<?> targetClass, int requestCode) {
        Intent intent = new Intent(this, targetClass);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 带返回值的跳转
     *
     * @param context context
     * @param clazz clazz
     * @param bundle bundle
     * @param reuqestCode reuqestCode
     */
    protected void startActivity(Context context, Class<? extends BaseActivity> clazz, Bundle bundle, int reuqestCode) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, reuqestCode);
    }

}
