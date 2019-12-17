package com.yizutiyu.test.fitnessbox.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.yizutiyu.test.fitnessbox.R;


/**
 * 自定义toast
 */
public class CustomToast {

    /** context */
    private Context mCtx;
    /** layout */
    private View mMainLayout;

    /** windowmanager */
    private WindowManager mWindowManager;
    /** params */
    private WindowManager.LayoutParams mWindowLayoutParams;
    /** handler */
    private Handler mHandler = new Handler();
    /** 无网络提示 */
    public static final int TOAST_TYPE_NO_NETWORK = 1;
    /** 查看下载进度 */
    public static final int TOAST_TYPE_DOWNLOAD = 2;
    /** 当前 type */
    private int mType = TOAST_TYPE_DOWNLOAD;
    /**
     * mShowTime
     */
    private long mShowTime;

    /**
     * 构造方法
     * @param context context
     * @param type type
     */
    public CustomToast(Context context, int type) {
        mCtx = context;
        mType = type;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        /** 展示时间 */
        // SUPPRESS CHECKSTYLE
        mShowTime = DateUtils.SECOND_IN_MILLIS * 5;
        mWindowManager = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < 24) { // SUPPRESS CHECKSTYLE
            mWindowLayoutParams.type =  WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        if (mType == TOAST_TYPE_NO_NETWORK) {
            mMainLayout = View.inflate(mCtx, R.layout.no_network_toast_layout, null);
            mMainLayout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            mMainLayout.findViewById(R.id.goto_setting).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (!AppUtils.startActivitySafely(v.getContext(), intent)) {
                        Toast.makeText(v.getContext(), R.string.cant_open_setting_page,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            mWindowLayoutParams.gravity = Gravity.CENTER;
        }
//        else {
//            mMainLayout = View.inflate(mCtx, R.layout.download_tip_layout, null);
//            mMainLayout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    close();
//                }
//            });
//            mWindowLayoutParams.y = mCtx.getResources().getDimensionPixelOffset(R.dimen.custom_toast_height);
//            mWindowLayoutParams.x = mCtx.getResources().getDimensionPixelOffset(R.dimen.libui_common_size_48);
//            mWindowLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER;
//        }
    }

    /**
     * 展示
     */
    public void show() {
        try {
            hasClosed = false;
            mWindowManager.addView(mMainLayout, mWindowLayoutParams);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }, mShowTime);
    }

    /** 关闭的标识 */
    private volatile boolean hasClosed = false;

    /**
     * 关闭
     */
    public void close() {
        if (hasClosed) {
            return;
        }
        try {
            mWindowManager.removeView(mMainLayout);
            hasClosed = true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
