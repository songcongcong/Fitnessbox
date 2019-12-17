package com.yizutiyu.test.fitnessbox.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.yizutiyu.test.fitnessbox.BuildConfig;
import com.yizutiyu.test.fitnessbox.GpsService;
import com.yizutiyu.test.fitnessbox.R;
import com.yizutiyu.test.fitnessbox.eventbus.GpsEventBus;
import com.yizutiyu.test.fitnessbox.sqlite.DataBaseOpenHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author
 * @date 2019-08-14.
 */
public class AppUtils {

    /**
     * TAG
     */
    private static final String TAG = "AppUtils";
    private static List<String> list;

    /**
     * @return data
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        // 获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }


    /**
     * 全透明栏
     *
     * @param activity activity
     */
    public static void setStatusBarFullTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) { //21表示5.0
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) { //19表示4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    /**
     * 检查是否有权限
     *
     * @param activity   activity
     * @param mReestCode mReestCode
     */
    public static void requestPermission(Activity activity, int mReestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) { //未开启定位权限
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    mReestCode);
        }
    }

    /**
     * 弹出对话框
     *
     * @param activity activity
     */
    public static void showWaringDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("警告！")
                .setMessage("请前往设打开定位权限，否则无法获取精 确位置！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                        activity.finish();
                    }
                }).show();
    }

    /**
     * 设置状态栏透明
     *
     * @param activity activity
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
//            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            // 设置沉浸式
           /* int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);*/
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.color_actionbar));
            //导航栏颜色也可以正常设置
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            // attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
        }
    }

    /**
     * 调用网络请求
     *
     * @param context     context
     * @param longitude   longitude
     * @param latitude    latitude
     * @param preferences preferences
     */
    @SuppressLint("SimpleDateFormat")
    public static synchronized void requestUrl(final Context context, final double longitude,
                                               final double latitude, final SharedPreferences preferences) {
        OkHttp3Util.doPost(RequestUrl.mUrl + "/ezapp/saveCoordinates", getParmes(longitude, latitude, preferences),
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "定位失败：" + e.toString());
                        }
//                        Looper.prepare();
//                        setSqlite(longitude, latitude, context, preferences);
//                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "定位成功OkHttp3Util：" + response.body().string() + ":" + AppUtils.getCurrentTime());
                        }
                    }
                }, context);

    }

    private  static  boolean isFlag;

    /**
     * 调用网络请求
     *
     * @param context     context
     * @param longitude   longitude
     * @param latitude    latitude
     * @param preferences preferences
     */
    @SuppressLint("SimpleDateFormat")
    public static synchronized void requestAddressUrl(final Context context, final double longitude,
                                                      final double latitude, final SharedPreferences preferences) {
        Log.d(TAG, "接收到的点：" + longitude + ",:" + latitude);

        OkHttp3Util.doPost(RequestUrl.mUrl + "ezapp/saveCoordinate", getParmes(longitude, latitude, preferences),
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "实时数据失败：" + e.toString());
                        }
//                        Looper.prepare();
//                        setSqlite(longitude, latitude, context, preferences);
//                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "实时数据成功OkHttp3Util：" + response.body().string()
                                    + ",:" + AppUtils.getCurrentTime());
//                            Looper.prepare();
//                            EventBus.getDefault().post(new GpsEventBus(longitude, latitude));
//                            Looper.loop();
//                            isFlag = true;
                        }
                    }
                }, context);

    }


    private static void setSqlite(double longitude, double latitude, Context context, SharedPreferences sharedPreferences) {
        Toast.makeText(context, "存储：" + longitude + ",:" + latitude, Toast.LENGTH_LONG).show();
//                AppUtils.requestUrl(GpsService.this, locData.longitude, locData.latitude, mSharedPreferences);
//                AppUtils.requestAddressUrl(GpsService.this, locData.longitude, locData.latitude, mSharedPreferences);
        list = new ArrayList<>();
        list.add(GpsService.sql_message);
        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        values.put("longitude", longitude);
        values.put("lat", latitude);
        DataBaseOpenHelper gps = DataBaseOpenHelper.getInstance(context, GpsService.mDbTableName, 1, list);
        // 删除数据库里的数据
        gps.delete(GpsService.mDbTableName, null, null);
        gps.insert(GpsService.mDbTableName, values);
        Cursor cursor = gps.query(GpsService.mDbTableName, new String[]{"longitude", "lat"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String mLongitude = cursor.getString(cursor.getColumnIndex("longitude"));
            String lat = cursor.getString(cursor.getColumnIndex("lat"));
//            Log.d("song", "取出定位：" + longitude + ":" + lat);
            Toast.makeText(context, "从数据库取出：" + mLongitude + ",:" + lat, Toast.LENGTH_LONG).show();
            AppUtils.requestUrl(context, Double.valueOf(mLongitude), Double.valueOf(lat), sharedPreferences);
            AppUtils.requestAddressUrl(context, Double.valueOf(longitude), Double.valueOf(lat), sharedPreferences);
        }
        cursor.close();
    }

    /**
     * post 参数
     *
     * @param longitude   longitude
     * @param latitude    latitude
     * @param preferences preferences
     * @return map
     */
    public static HashMap<String, String> getParmes(double longitude, double latitude, SharedPreferences preferences) {
        String userid = preferences.getString("userid", "");
        HashMap<String, String> parmes = new HashMap<>();
        if (!TextUtils.isEmpty(String.valueOf(longitude)) && !TextUtils.isEmpty(String.valueOf(latitude))) {
            parmes.put("longitude", String.valueOf(longitude));
            parmes.put("latitude", String.valueOf(latitude));
            parmes.put("currenttime", AppUtils.getCurrentTime());
            parmes.put("userId", userid);
        }
        return parmes;
    }

    /**
     * 启动activity时，可能会出现异常，这里写一个通用方法，保证来Catch住异常
     *
     * @param context Context
     * @param intent  Intent
     * @return 是否启动成功
     */
    public static boolean startActivitySafely(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 网络错误，跳转到错误页面
     *
     * @param activity activity
     * @param webView  webView
     */
    public static void setNetWorkView(final Activity activity, final WebView webView) {
        webView.reload();
        webView.loadUrl("about:blank"); // 避免出现默认的错误界面
        webView.removeAllViews();

        final View inflate = View.inflate(activity, R.layout.newtwork_layout, null);
        webView.addView(inflate);
        final TextView mCancle = inflate.findViewById(R.id.close);
        TextView mSure = inflate.findViewById(R.id.goto_setting);
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils.GoSetting(activity);
            }
        });
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return int
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * @param activity    activity
     * @param permissions 权限数组
     * @param requestCode 申请码
     * @return true 有权限  false 无权限
     */
    public static boolean checkAndApplyfPermissionActivity(Activity activity, String[] permissions,
                                                           int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions = checkPermissions(activity, permissions);
            if (permissions != null && permissions.length > 0) {
                activity.requestPermissions(permissions, requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * @param context     上下文
     * @param permissions 权限数组
     * @return 还需要申请的权限
     */
    private static String[] checkPermissions(Context context, String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return new String[0];
        }
        ArrayList<String> permissionLists = new ArrayList<>();
        permissionLists.addAll(Arrays.asList(permissions));
        for (int i = permissionLists.size() - 1; i >= 0; i--) {
            if (ContextCompat.checkSelfPermission(context, permissionLists.get(i))
                    == PackageManager.PERMISSION_GRANTED) {
                permissionLists.remove(i);
            }
        }

        String[] temps = new String[permissionLists.size()];
        for (int i = 0; i < permissionLists.size(); i++) {
            temps[i] = permissionLists.get(i);
        }
        return temps;
    }

    /**
     * 检查申请的权限是否全部允许
     *
     * @param grantResults 权限
     * @return 结果
     */
    public static boolean checkPermission(int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            return true;
        } else {
            int temp = 0;
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_GRANTED) {
                    temp++;
                }
            }
            return temp == grantResults.length;
        }
    }
}
