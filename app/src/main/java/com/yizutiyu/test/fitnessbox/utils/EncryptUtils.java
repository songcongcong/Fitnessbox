package com.yizutiyu.test.fitnessbox.utils;


import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 用于针对具体业务需求加密
 *  cmj  2019-07-04
 */
public class EncryptUtils {

    /**
     * 店铺Id 加密，加密token生命周期为一天
     *
     * @param storeId 店铺id
     * @return
     */
    public static String encryptStoreId(String storeId) {
        if (!StringUtils.isBlank(storeId)) {
            Map<String, Object> map = new HashMap<>();

            //建立载荷，这些数据根据业务，自己定义。
            map.put("storeId", storeId);
            //生成时间
            map.put("sta", new Date().getTime());
            //过期时间
            map.put("exp", new Date().getTime() + 24 * 60 * 60 * 1000);
            try {
                String token = TokenUtils.creatToken(map);
                return token;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 解密 店铺id token
     *
     * @param token
     * @return :案例：
     * {
     * data={"storeId":"1233411200wwr","sta":1562223971296,"exp":1562310371296}, # data对应storeId信息,
     * Result=0: 取值状态有三种：0：代表成功，1代表秘钥修改，2：过期
     * }
     */
    public static Map<String, Object> encodingStoreId(String token) {
        try {
            final Map<String, Object> map = TokenUtils.valid(token);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 加密主键id
     *
     * @param simpleId id主键
     * @return 加密后的token
     */
    public static String encryptSimpleId(String simpleId) {
        if (!StringUtils.isBlank(simpleId)) {
            Map<String, Object> map = new HashMap<>();

            //建立载荷，这些数据根据业务，自己定义。
            map.put("simpleId", simpleId);
            //生成时间
            map.put("sta", new Date().getTime());
            //过期时间
            map.put("exp", new Date().getTime() + Integer.MAX_VALUE);
            try {
                String token = TokenUtils.creatToken(map);
                return token;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    /**
     * 解密encryptSimpleId 加密主键id后的 token
     * return 解密后的主键id
     */
    public static String encodingSimpleId(String token) {
        try {
            final Map<String, Object> map = TokenUtils.valid(token);
            Object data = map.get("data");
            if (data instanceof Map) {
                Map data_ = (Map) data;
                Object object = data_.get("simpleId");
                return object.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("song","Exception:"+e.toString());
        }
        return null;
    }
}
