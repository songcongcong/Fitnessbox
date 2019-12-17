package com.yizutiyu.test.fitnessbox;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * @author songcongcong
 * @date 2019-08-14.
 */
public class SplashActivity extends BaseActivity {
    /**
     * handler
     */
    private final MyHandler handler = new MyHandler(this);

    /**
     * MyHandler
     */
    private static class MyHandler extends Handler {
        /**
         * mActivity
         */
        private final WeakReference<SplashActivity> mActivity;

        /**
         *
         * @param activity activity
         */
        public MyHandler(SplashActivity activity) {
            mActivity = new WeakReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        //跳转MainActivity
                        Intent mainIntent = new Intent(activity, MainActivity.class);
                        activity.startActivity(mainIntent);
                        activity.finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setView() {
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    protected void setData() {

    }
}
