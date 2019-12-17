package com.yizutiyu.test.fitnessbox.bean;

/**
 * 2019/9/3
 */
public class CallLogInfo {
    /**
     * number
     */
    public String number;
    /**
     * date
     */
    public long date;
    /**
     * type
     */
    public int type;

    /**
     * CallLogInfo
     *
     * @param number number
     * @param date   date
     * @param type   type
     */
    public CallLogInfo(String number, long date, int type) {
        super();
        this.number = number;
        this.date = date;
        this.type = type;
    }

    /**
     * CallLogInfo
     */
    public CallLogInfo() {
        super();
    }
}
