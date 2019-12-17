package com.yizutiyu.test.fitnessbox.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * activity管理者
 *
 * @author liyiyang
 * @functionModule AppCore基础
 * @since 2017-3-24
 */

public final class ActivityLifecycleManager implements Application.ActivityLifecycleCallbacks {

    /**
     * CREATED 状态
     */
    private static final int CREATED = 1;
    /**
     * RESUMED 状态
     */
    public static final int RESUMED = 2;
    /**
     * PAUSED 状态
     */
    private static final int PAUSED = 3;
    /**
     * STOPED 状态
     */
    private static final int STOPED = 4;

    /**
     * instance
     */
    private static volatile ActivityLifecycleManager sInstance = null;

    /**
     * activity 轨迹
     */
    private LinkedHashMap<Activity, Integer> mActivityStack;

    /**
     * 助手被切到前台的时间
     */
    private long mToForegroundTime;

    /**
     * handler
     */
    private Handler mHandler;

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static ActivityLifecycleManager getInstance() {
        if (sInstance == null) {
            synchronized (ActivityLifecycleManager.class) {
                if (sInstance == null) {
                    sInstance = new ActivityLifecycleManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 构造方法
     */
    private ActivityLifecycleManager() {
        mActivityStack = new LinkedHashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        synchronized (sInstance) {
            if (mActivityStack.isEmpty()) {
                mToForegroundTime = System.currentTimeMillis();
            }
            mActivityStack.put(activity, CREATED);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        synchronized (this) {
            mActivityStack.put(activity, RESUMED);
        }
        if (mToForegroundTime == 0) {
            mToForegroundTime = System.currentTimeMillis();
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {
        synchronized (sInstance) {
            mActivityStack.put(activity, PAUSED);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        synchronized (sInstance) {
            mActivityStack.put(activity, STOPED);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity last;
                boolean state;
                synchronized (sInstance) {
                    last = getLastActivity();
                    state = last != null && mActivityStack.get(last) != RESUMED;
                }
                if (state && mToForegroundTime != 0) {
                    // 添加统计，同时统计此次使用时长，时长单位为秒
                    long timeSpan = (System.currentTimeMillis() - mToForegroundTime) / 1000; // SUPPRESS CHECKSTYLE
//                    StatisticProcessor.addOnlyValueUEStatisticCache(last,
//                            AppsCoreStatisticConstants.UEID_030402,
//                            String.valueOf(timeSpan));
                    mToForegroundTime = 0;
                }

            }
        }, 500); // SUPPRESS CHECKSTYLE

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        boolean isEmpty;
        synchronized (sInstance) {
            mActivityStack.remove(activity);
            isEmpty = mActivityStack.isEmpty();
        }
        if (isEmpty) {
            // 添加统计，同时统计此次使用时长，时长单位为秒
            long timeSpan = (System.currentTimeMillis() - mToForegroundTime) / 1000; // SUPPRESS CHECKSTYLE
//            StatisticProcessor.addOnlyValueUEStatisticCache(activity,
//                    AppsCoreStatisticConstants.UEID_030402,
//                    String.valueOf(timeSpan));
            mToForegroundTime = 0;

        }
    }

    /**
     * 是否在前台
     *
     * @return 是否在前台
     */
    public boolean isAppForeGround() {
        synchronized (sInstance) {
            if (!mActivityStack.isEmpty()) {
                Map.Entry<Activity, Integer> last = null;
                Iterator iterator = mActivityStack.entrySet().iterator();
                while (iterator.hasNext()) {
                    last = (Map.Entry<Activity, Integer>) iterator.next();
                }
                return last.getValue() == RESUMED;
            }
        }
        return false;
    }

    /**
     * 跟日志相关
     * 返回activity队列
     *
     * @return activity队列
     */
    public LinkedHashMap<Activity, Integer> getActivityStack() {
        synchronized (sInstance) {
            return new LinkedHashMap(mActivityStack);
        }
    }

    /**
     * 返回acitivty队列是否为空
     *
     * @return acitivty队列是否为空
     */
    public boolean isHasActivity() {
        synchronized (sInstance) {
            return !mActivityStack.isEmpty();
        }
    }

    /**
     * 返回最上层的activity
     *
     * @return 最上层的activity
     */
    public Activity getLastActivity() {
        synchronized (sInstance) {
            if (!mActivityStack.isEmpty()) {
                Map.Entry<Activity, Integer> last = null;
                Iterator iterator = mActivityStack.entrySet().iterator();
                while (iterator.hasNext()) {
                    last = (Map.Entry<Activity, Integer>) iterator.next();
                }
                return last.getKey();
            }
        }
        return null;
    }


    /**
     * 返回activity队列中特定activity的实例
     *
     * @param cls cls
     * @return activity队列中cls的实例
     */
    public Activity getTargetActivity(Class cls) {
        Activity target = null;
        synchronized (sInstance) {
            Iterator iterator = mActivityStack.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Activity, Integer> entry = (Map.Entry<Activity, Integer>) iterator.next();
                Activity activity = entry.getKey();
                if (cls.isAssignableFrom(activity.getClass())) {
                    target = activity;
                    break;
                }
            }
        }
        return target;
    }

    /**
     * 清栈
     */
    public void clearStack() {
        synchronized (sInstance) {
            if (!mActivityStack.isEmpty()) {
                LinkedHashMap<Activity, Integer> copy = new LinkedHashMap<>(mActivityStack);
                Iterator iterator = copy.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Activity, Integer> entry = (Map.Entry<Activity, Integer>) iterator.next();
                    Activity activity = entry.getKey();
                    activity.finish();
                }
                copy.clear();
            }
        }
    }

    /**
     * 获得队列activity的个数
     *
     * @return 队列activity的个数
     */
    public int getActivityStackSize() {
        synchronized (sInstance) {
            return mActivityStack.size();
        }
    }

}
