package com.yizutiyu.test.fitnessbox.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 王佳 on 15-12-17.
 * 时间工具类，各种处理时间格式转换的静态方法
 * @functionModule 消息中心
 */
public final class TimeUtil {

    /** 毫秒 */
    public static final long MILLIS = 1;
    /** 秒 */
    public static final long SECOND = 1000 * MILLIS;
    /** 分 */
    public static final long MINUTE = 60 * SECOND;
    /** 时 */
    public static final long HOUR = 60 * MINUTE;
    /** 天 */
    public static final long DAY = 24 * HOUR;
    /** 周 */
    public static final long WEEK = 7 * DAY;
    /** 月 */
    public static final long MONTH = 30 * DAY;
    /** 年 */
    public static final long YEAR = 365 * DAY;
    /** logtag */
    private static final String LOG_TAG = TimeUtil.class.getSimpleName();

    /**
     * 私有构造函数
     */
    private TimeUtil() {
    }

    /**
     * 获得格式化的时间
     *
     * @param timeStamp 10位秒级时间戳
     * @return 格式化的时间
     */
    public static String formatTime(long timeStamp) {
        timeStamp *= SECOND;
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timeStamp;
        if (diff < YEAR) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timeStamp));
        } else {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(timeStamp));
        }
    }

    /**
     * 获得格式化的时间
     *
     * @param timeStamp 时间戳(秒级)
     * @return 格式化的时间
     */
    public static String formatTimeByDifferentYear(long timeStamp) {
        timeStamp *= SECOND;
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int nowYear = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(timeStamp);
        int oldYear = calendar.get(Calendar.YEAR);

        if (nowYear == oldYear) {
            return new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date(timeStamp));

        } else {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(timeStamp));
        }
    }

    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTimeData(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder sb = new StringBuilder();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+"毫秒");
        }
//        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");//初始化Formatter的转换格式。

//        String hms = formatter.format(sb.toString());
        Log.d("song","毫秒："+sb.toString());
        return sb.toString();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        // 获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }


    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     *                 ==1----天，时，分。 ==2----时
     * @return 返回时间差
     */
    public static String getTimeDifference(String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                    - min * 60 * 1000 - s * 1000);
            // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
            // "秒");
            long hour1 = diff / (60 * 60 * 1000);
            String hourString = hour1 + "";
            long min1 = ((diff / (60 * 1000)) - hour1 * 60);
            if (hour1 > 0) {
                timeString = hour1 + "小时" + min1 + "分";
            } else {
                timeString = min1 + "分";
            }

             System.out.println(day + "天" + hour + "小时" + min + "分" + s +
             "秒");

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }


    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     *                 ==1----天，时，分。 ==2----时
     * @return 返回时间差
     */
    public static String getTimeDifferencePhone(String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                    - min * 60 * 1000 - s * 1000);
            // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
            // "秒");
            long hour1 = diff / (60 * 60 * 1000);
            String hourString = hour1 + "";
            long min1 = ((diff / (60 * 1000)) - hour1 * 60);
            if (hour1 > 0) {
                timeString = hour1 + "小时" + min1 + "分钟";
            } else {
                timeString = min1 + "分钟";
            }

            System.out.println(day + "天" + hour + "小时" + min + "分" + s +
                    "秒");

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }



    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1
     * 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     * 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2)
            throws java.text.ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = { day, hour, min, sec };
        return times;
    }



}
