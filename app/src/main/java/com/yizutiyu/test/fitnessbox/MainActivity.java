package com.yizutiyu.test.fitnessbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yizutiyu.test.fitnessbox.bean.UserInfo;
import com.yizutiyu.test.fitnessbox.utils.AppUtils;
import com.yizutiyu.test.fitnessbox.utils.EncryptUtils;
import com.yizutiyu.test.fitnessbox.utils.RequestUrl;
import com.yizutiyu.test.fitnessbox.utils.TimeUtil;


/**
 * @author
 * @date 2019-08-14.
 */
public class MainActivity extends BaseActivity {

    /**
     * TAG
     */
    private static final String TAG = "MainActivity";
    /**
     * mWebView
     */
    public static WebView mWebView;
    /**
     * mEdit
     */
    public static SharedPreferences.Editor mEdit;
    /**
     * mSharedPreferences
     */
    public static SharedPreferences mSharedPreferences;

    /**
     * webSettings
     */
    private WebSettings webSettings;
    /**
     * mClose
     */
    private TextView mClose;
    /**
     * mSetting
     */
    private TextView mSetting;
    /**
     * mLayout
     */
    private RelativeLayout mLayout;

    /**
     *  潜客/会员手机号
     */
    private String mTel;
    /**
     * 潜客id
     */
    private static String mGuestId;
    /**
     *  会员id
     */
    private static String mMemberID;
    /**
     * 判断是否来自潜客
     */
    private static boolean isPhone;
    /**
     * 判断是否来自会员
     */
    private static boolean isMemPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void setView() {
        init();
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void setData() {
        initWebView();
        getWebViewClient();
        getWebChromeClient();
        getLogin();
        mWebView.addJavascriptInterface(this, "call");
        mWebView.addJavascriptInterface(this, "isLogin");
        mWebView.addJavascriptInterface(this, "callMem");
    }

    /**
     * onResume
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtils.checkAndApplyfPermissionActivity(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE}, 100)) {
            Log.d(TAG, "有定位权限");
        }

    }

    /**
     * 初始化
     */
    @SuppressLint("CommitPrefEdits")
    public void init() {
        Log.i("song", "registerIt");
        mWebView = findViewById(R.id.webview);
        mLayout = findViewById(R.id.main_layout);
        mClose = findViewById(R.id.close);
        mSetting = findViewById(R.id.goto_setting);

        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        mEdit = mSharedPreferences.edit();
    }


    /**
     * 拨打电话----潜客列表
     * @param tel  tel
     * @param id  id
     */
    @SuppressLint("AddJavascriptInterface")
    @JavascriptInterface
    public void phone(String tel, String id) {
        if (!TextUtils.isEmpty(tel) && !TextUtils.isEmpty(id)) {
            isPhone = true;
            this.mTel = tel;
            mGuestId = id;
            if (AppUtils.checkAndApplyfPermissionActivity(this, new String[]{Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE}, 101)) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }


    /**
     * 拨打电话----会员列表
     * @param phone phone
     * @param id id
     */
    @SuppressLint("AddJavascriptInterface")
    @JavascriptInterface
    public void Memphone(String phone, String id) {
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(id)) {
            isMemPhone = true;
            mMemberID = id;
            this.mTel = phone;
            if (AppUtils.checkAndApplyfPermissionActivity(this, new String[]{Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE}, 101)) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }


    /**
     * 点击登录时，登陆成功
     *
     * @param userInfo userInfo
     */
    @SuppressLint("AddJavascriptInterface")
    @JavascriptInterface
    public void userInfo(String userInfo) {
        try {
            UserInfo userInfo1 = new Gson().fromJson(userInfo, UserInfo.class);
            String userid = EncryptUtils.encodingSimpleId(String.valueOf(userInfo1.getId()));
            mEdit.putString("userid", userid).apply();
            mEdit.putBoolean("islogin", true).apply();
            mEdit.remove("loginpage").apply();
            Log.d(TAG,"登录");
            if (AppUtils.checkAndApplyfPermissionActivity(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE}, 100)) {
                Intent intent = new Intent(this, GpsService.class);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 权限回调
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (AppUtils.checkPermission(grantResults)) {
            switch (requestCode) {
                case 100:
                    Log.e(TAG, "===========权限回调---用户同意了");
                    break;
                case 101:
                    if (!TextUtils.isEmpty(mTel) && (!TextUtils.isEmpty(mGuestId) || !TextUtils.isEmpty(mMemberID))) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mTel));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    Log.e(TAG, "权限回调---用户同意了" + ",id:" + ",:" + mTel + ":" + mMemberID + ",:" + mGuestId);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 登录方法
     */
    private void getLogin() {
        boolean islogin = mSharedPreferences.getBoolean("islogin", false);
        if (islogin) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, GpsService.class);
                startService(intent);
            }
            mWebView.loadUrl(RequestUrl.mUrl + "ezapp/mine");
            mEdit.remove("loginpage").apply();
        } else {
            boolean mLoginPage = mSharedPreferences.getBoolean("loginpage", false);
            if (mLoginPage) {
                Log.d(TAG, "getLogin-------我的-----");
                mWebView.loadUrl(RequestUrl.mUrl + "ezapp/mine");
            } else {
                Log.d(TAG, "getLogin-------登录-----");
                mEdit.putBoolean("loginpage", true).apply();
//                mWebView.loadUrl(RequestUrl.mUrl + "ezapp");
                mWebView.loadUrl(RequestUrl.mUrl + "ezapp/ezuAppLogin");

            }
        }
    }

    /**
     * webViewClient
     */
    private void getWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // 信任SSL证书，https请求
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (handler != null) {
                    handler.proceed();
                }
            }
            //            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                int errorCode = error.getErrorCode();
//                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT
//                        || errorCode == ERROR_TIMEOUT) {
//                    Log.d("song","onReceivedError");
//                    mWebView.setVisibility(View.GONE);
//                    mLayout.setVisibility(View.VISIBLE);
//                    mClose.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            finish();
//                        }
//                    });
//                    mSetting.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            NetWorkUtils.GoSetting(MainActivity.this);
//                        }
//                    });
//                }
//            }
        });
    }

    /**
     * WebChromeClient
     */
    private void getWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            //设置响应js 的Confirm()函数
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                result.confirm();
                Log.e("song", "响应函数");
                return true;
            }

            //设置响应js 的Prompt()函数
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                      final JsPromptResult result) {
                result.confirm();
                Log.e("song", "555响应函数");

                return true;
            }
        });
    }

    /**
     * 初始化webview
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        // 清缓存和记录，缓存引起的白屏
        mWebView.clearCache(true);
        mWebView.clearHistory();

        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setAllowFileAccess(true);
        webSettings.setSavePassword(true);
        webSettings.setSupportZoom(true);
        //解决对某些标签的不支持出现白屏
        webSettings.setDomStorageEnabled(true);
        // 缓存白屏
        String appCachePath = getApplicationContext().getCacheDir()
                .getAbsolutePath() + "/webcache";
        // 设置 Application Caches 缓存目录
        webSettings.setAppCachePath(appCachePath);
        webSettings.setDatabasePath(appCachePath);
        //解决定位在地图上不显示
        webSettings.setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(dir);

        mWebView.requestFocus();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.removeAllViews();
        }
        if (mEdit != null) {
            mEdit.remove("loginpage").apply();
        }
        Log.d(TAG, "销毁");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    /**
     * 监听电话状态广播
     */
    public static class BroadcastReceiverMgr extends BroadcastReceiver {
        /**
         * TAG
         */
        private static final String TAG = "BroadcastReceiverMgr";
        /**
         * mIncomingNumber
         */
        private String mIncomingNumber;

        @Override
        public void onReceive(Context context, Intent intent) {
            // 如果是拨打电话
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                // 手机号
                Log.d(TAG, "拨号：手机号：" + phoneNumber);
            } else {
                // 如果是来电
                TelephonyManager tManager = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tManager.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING: // 电话响铃
                        mIncomingNumber = intent.getStringExtra("incoming_number");
                        Log.d(TAG, "电话响铃 :" + mIncomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: //来电接通，或者去电，去电接通，没法区分
                        mEdit.putString("currentdata", TimeUtil.getCurrentTime()).apply();
                        mEdit.putBoolean("iscall", true).apply();
                        Log.d(TAG, "incoming ACCEPT 来电接通:" + mIncomingNumber + ":" + TimeUtil.getCurrentTime());
                        break;
                    case TelephonyManager.CALL_STATE_IDLE: // 电话挂断
                        // 获取电话号码
                        boolean iscall = mSharedPreferences.getBoolean("iscall", false);
                        if (iscall) {
                            String startDate = mSharedPreferences.getString("currentdata", "");
                            String endDate = TimeUtil.getCurrentTime();
                            final String currentTime = TimeUtil.getTimeDifferencePhone(startDate, endDate);
                            Log.d(TAG, "incoming IDLE--电话挂断：" + startDate + ",:   " + endDate
                                    + ",:   " + currentTime + ",:;" + mGuestId + ",:" + mMemberID);
                            if (!TextUtils.isEmpty(mGuestId) && isPhone) {
                                Log.d(TAG, "潜客" + ",:");
                                // 必须另开线程进行JS方法调用(否则无法调用)
                                mWebView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 调用javascript的callJS()方法
                                        mWebView.loadUrl("javascript:getCallTime('" + currentTime
                                                + "','" + mGuestId + "')");

                                        //  清除保存的当前时间和状态
                                        mEdit.remove("currentdata").apply();
                                        mEdit.remove("iscall").apply();
                                    }
                                });
                            }
                            if (!TextUtils.isEmpty(mMemberID) && isMemPhone) {
                                // 必须另开线程进行JS方法调用(否则无法调用)
                                mWebView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 调用javascript的callJS()方法
                                        mWebView.loadUrl("javascript:getMemCallTime('" + currentTime
                                                + "','" + mMemberID + "')");

                                        //  清除保存的当前时间和状态
                                        mEdit.remove("currentdata").apply();
                                        mEdit.remove("iscall").apply();
                                    }
                                });
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
