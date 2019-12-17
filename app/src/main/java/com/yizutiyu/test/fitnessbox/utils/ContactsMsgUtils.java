package com.yizutiyu.test.fitnessbox.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.yizutiyu.test.fitnessbox.bean.CallLogInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取通话记录，时长等
 */
public class ContactsMsgUtils {
    public List<CallLogInfo> getCallLog(Context context) {
        List<CallLogInfo> infos = new ArrayList<CallLogInfo>();
        ContentResolver cr = context.getContentResolver();
        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[] { CallLog.Calls.NUMBER, CallLog.Calls.DATE,
                CallLog.Calls.TYPE };
        Cursor cursor = cr.query(uri, projection, null, null, CallLog.Calls.DATE  + " desc");
        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            long date = cursor.getLong(1);
            int type = cursor.getInt(2);
            infos.add(new CallLogInfo(number, date, type));
        }
        cursor.close();
        return infos;
    }

}
