package com.yizutiyu.test.fitnessbox;

import android.app.Application;

import android.support.multidex.MultiDex;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author songcongcong
 * @date 2019-08-14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        if (!"generic".equalsIgnoreCase(Build.BRAND)) {
//            SDKInitializer.initialize(getApplicationContext());
//        }
        MultiDex.install(this);
//        closeAndroidPDialog();
    }

    /**
     * closeAndroidPDialog
     */
    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
