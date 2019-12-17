package com.yizutiyu.test.fitnessbox.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yizutiyu.test.fitnessbox.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetWorkUtils {
 
    /** 网络不可用 */
    public static final int NO_NET_WORK = 0;
    /** 是wifi连接 */
    public static final int WIFI = 1;
    /** 不是wifi连接 */
    public static final int NO_WIFI = 2;
 
    private NetWorkUtils(){
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
 
    /**
     * 判断是否打开网络
     * @param context
     * @return
     */
    public static boolean isNetWorkAvailable(Context context){
        boolean isAvailable = false ;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isAvailable()){
            isAvailable = true;
        }
        return isAvailable;
    }
 
    /**
     * 获取网络类型
     * @param context
     * @return
     */
    public static int getNetWorkType(Context context) {
        if (!isNetWorkAvailable(context)) {
            return NetWorkUtils.NO_NET_WORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting())
            return NetWorkUtils.WIFI;
        else
            return NetWorkUtils.NO_WIFI;
    }
 
    /**
     * 判断当前网络是否为wifi
     * @param context
     * @return  如果为wifi返回true；否则返回false
     */
    @SuppressWarnings("static-access")
    public static boolean isWiFiConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo.getType() == manager.TYPE_WIFI ? true : false;
    }
 
    /**
     * 判断MOBILE网络是否可用
     * @param context
     * @return
     */
    public static boolean isMobileDataEnable(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileDataEnable = false;
        isMobileDataEnable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        return isMobileDataEnable;
    }
 
    /**
     * 判断wifi 是否可用
     * @param context
     * @return
     */
    public static boolean isWifiDataEnable(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiDataEnable = false;
        isWifiDataEnable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        return isWifiDataEnable;
    }
 
    /**
     * 跳转到网络设置页面
     * @param activity
     */
    public static void GoSetting(Context activity){
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!AppUtils.startActivitySafely(activity, intent)) {
            Toast.makeText(activity, R.string.cant_open_setting_page,
                    Toast.LENGTH_LONG).show();
        }
    }
 
    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cn);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * 网络相关的工具类
     */
    public static final class NetUtility {

        /**
         * 获取Active的网络信息
         * @param context Context
         * @return networkInfo
         */
        @TargetApi(Build.VERSION_CODES.M)
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        public static NetworkInfo getActiveNetworkInfoSafely(Context context) {
            NetworkInfo info = null;
            try {
                Context appContext = context.getApplicationContext();
                ConnectivityManager manager = (ConnectivityManager) appContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                info = manager.getActiveNetworkInfo();
            } catch (Exception e) {
//                if (DEBUG) {
                    e.printStackTrace();
//                }
            }
            return info;
        }

        /**
         * 检查当前是否有可用网络
         *
         * @param context Context
         * @return true 表示有可用网络，false 表示无可用网络
         */
        @TargetApi(Build.VERSION_CODES.M)
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        public static boolean isNetWorkEnabled(Context context) {
            try {
                ConnectivityManager connectivity = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivity != null) {
                    NetworkInfo info = connectivity.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        // 当前网络是连接的
                        if (info.getState() == NetworkInfo.State.CONNECTED) { // 当前所连接的网络可用
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
//                if (DEBUG) {
                    e.printStackTrace();
//                }
                return true;
            }
            return false;
        }

        /**
         * 判断当前网络类型是否是wifi
         *
         * @param context Context
         * @return true 是wifi网络，false 非wifi网络
         */
        public static boolean isWifiNetWork(Context context) {
            String networktype = "NotAvaliable";
            NetworkInfo networkinfo = getActiveNetworkInfoSafely(context);
            if (networkinfo != null && networkinfo.isAvailable()) {
//                if (DEBUG) {
                    Log.d("song", "netWorkInfo: " + networkinfo);
//                }
                networktype = networkinfo.getTypeName().toLowerCase();
                if (networktype.equalsIgnoreCase("wifi")) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 获取运营商类型
         *
         * @param context context
         * @return network.html-operator-name或空
         */
        public static String getNetworkOperatorName(Context context) {
            String nwOperatorName = "";
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            nwOperatorName = tm.getNetworkOperatorName();
            return nwOperatorName;
        }

        /**
         * 取得网络类型，wifi 2G 3G
         *
         * @param context context
         * @return WF 2G 3G 4G，或空 如果没网
         */
        public static String getWifiOr2gOr3G(Context context) {
            String networkType = "";
            if (context != null) {
                NetworkInfo activeNetInfo = getActiveNetworkInfoSafely(context);
                if (activeNetInfo != null && activeNetInfo.isConnectedOrConnecting()) { //  有网
                    networkType = activeNetInfo.getTypeName().toLowerCase();
                    if (networkType.equals("wifi")) {
                        networkType = "WF";
                    } else { //  移动网络
                        //  // 如果使用移动网络，则取得apn
                        //                     apn = activeNetInfo.getExtraInfo();
                        //  将移动网络具体类型归一化到2G 3G 4G
                        networkType = "2G"; //  默认是2G
                        int subType = activeNetInfo.getSubtype();
                        switch (subType) {
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                                networkType = "3G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_CDMA:  //  IS95
                                break;
                            case TelephonyManager.NETWORK_TYPE_EDGE:  //  2.75
                                break;
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                networkType = "3G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                networkType = "3G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_GPRS:  //  2.5
                                break;
                            case TelephonyManager.NETWORK_TYPE_HSDPA:  //  3.5
                                networkType = "3G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_HSPA:    //  3.5
                                networkType = "3G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                                networkType = "3G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                                networkType = "3G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                                networkType = "3G";
                                break; //  ~ 1-2 Mbps
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                networkType = "3G";
                                break; //  ~ 5 Mbps
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                networkType = "3G";
                                break; //  ~ 10-20 Mbps
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                break; //  ~25 kbps
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                networkType = "4G";
                                break; //  ~ 10+ Mbps
                            default:
                                break;
                        }
                    } //  end 移动网络if
                } //  end 有网的if
            }
            return networkType;
        }

        /**
         * 得到当前网络的dns服务地址
         *
         * @param ctx Context
         * @return dns
         */
        public static String getDNS(Context ctx) {
            String dns = "";
            try {
                if (isWifiNetWork(ctx)) {
                    Context appContext = ctx.getApplicationContext();
                    WifiManager wifi = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
                    if (wifi != null) {
                        DhcpInfo info = wifi.getDhcpInfo();
                        if (info != null) {
                            dns = intToInetAddress(info.dns1).getHostAddress();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dns;
        }

        /**
         * 获取手机IP信息
         *
         * @param skipipv6 是否忽略ipv6地址
         *
         * @return info ip地址
         */
        public static String getIpInfo(boolean skipipv6) {
            String ipInfo = null;

            try {
                Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
                LOOP:
                while (faces.hasMoreElements()) {
                    Enumeration<InetAddress> addresses = faces.nextElement().getInetAddresses();

                    while (addresses.hasMoreElements()) {
                        InetAddress inetAddress = addresses.nextElement();

                        if (skipipv6) {
                            if (inetAddress instanceof Inet6Address) {
                                continue;
                            }
                        }

                        if (!inetAddress.isLoopbackAddress()) {
                            ipInfo = inetAddress.getHostAddress().toString();

                            break LOOP;
                        }
                    }
                }

            } catch (Exception e) {
//                if (DEBUG) {
                    Log.e("song", "getIpInfo fail!" + e.toString());
//                }
            }

            if (TextUtils.isEmpty(ipInfo)) {
                ipInfo = "";
            }

            return ipInfo;
        }

//        /**
//         * 接收网络数据流,兼容gzip与正常格式内容。
//         *
//         * @param is 读取网络数据的流
//         * @return 字符串类型数据
//         */
//        public static String recieveData(InputStream is) {
//            String s = null;
//            boolean isGzip = false;
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            if (is == null) {
////                if (DEBUG) {
//                    Log.d("song", "recieveData inputstream is null.");
////                }
//                return null;
//            }
//
//            try {
//                byte[] filetype = new byte[4]; //  SUPPRESS CHECKSTYLE
//                //  os = new BufferedOutputStream();
//                byte[] buff = new byte[BUFFERSIZE];
//                int readed = -1;
//                while ((readed = is.read(buff)) != -1) {
//                    baos.write(buff, 0, readed);
//                }
//                byte[] result = baos.toByteArray();
//                // 判断是否是gzip格式的内容。
//                System.arraycopy(result, 0, filetype, 0, 4); //  SUPPRESS CHECKSTYLE
//                if ("1F8B0800".equalsIgnoreCase(StringUtility.bytesToHexString(filetype))) {
//                    isGzip = true;
//                } else {
//                    isGzip = false;
//                }
//                if (DEBUG) {
//                    Log.d(TAG, " received file is gzip:" + isGzip);
//                }
//                if (isGzip) {
//                    result = GzipUtility.unGZip(result);
//                }
//                if (result == null) {
//                    return null;
//                }
//                s = new String(result, "utf-8");
//            } catch (Exception e) {
//                if (DEBUG) {
//                    e.printStackTrace();
//                }
//            } finally {
//                try {
//                    is.close();
//                    baos.close();
//                } catch (IOException e) {
//                    if (DEBUG) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            if (DEBUG) {
//                Log.i(TAG, "服务器下发数据:" + s);
//            }
//            return s;
//        }

        /**
         * 在获取wifi信息时，如果手机没有Wifi设置，可能会出现异常，这里写一个通用方法，保证来Catch住异常
         *
         * @param wifiManager WifiManager
         * @return WifiInfo
         */
        public static WifiInfo getWifiInfoSafely(WifiManager wifiManager) {
            WifiInfo info = null;

            try {
                info = wifiManager.getConnectionInfo();
            } catch (Exception e) {
//                if (DEBUG) {
                    e.printStackTrace();
//                }
            }
            return info;
        }

        /**
         * GPRS移动网络开关
         *
         * @param context 上下文
         * @param enabled true开false关
         */
        public static void toggleMobileData(Context context, boolean enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity");
                intent.setComponent(componentName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    context.startActivity(intent);

                } catch (ActivityNotFoundException ex) {

                    // The Android SDK doc says that the location settings activity
                    // may not be found. In that case show the general settings.

                    // General settings activity
                    intent.setAction(Settings.ACTION_SETTINGS);
                    intent.setComponent(null);
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
//                        if (DEBUG) {
                            e.printStackTrace();
//                        }
                    }
                } catch (Exception e) {
//                    if (DEBUG) {
                        e.printStackTrace();
//                    }
                }
            } else {
                try {
                    Context appContext = context.getApplicationContext();
                    ConnectivityManager conMgr = (ConnectivityManager) appContext
                            .getSystemService(Context.CONNECTIVITY_SERVICE);

                    Class<?> conMgrClass = null; //  ConnectivityManager类
                    Field iConMgrField = null; //  ConnectivityManager类中的字段
                    Object iConMgr = null; //  IConnectivityManager类的引用
                    Class<?> iConMgrClass = null; //  IConnectivityManager类
                    Method setMobileDataEnabledMethod = null; //  setMobileDataEnabled方法

                    //  取得ConnectivityManager类
                    conMgrClass = Class.forName(conMgr.getClass().getName());
                    //  取得ConnectivityManager类中的对象mService
                    iConMgrField = conMgrClass.getDeclaredField("mService");
                    //  设置mService可访问
                    iConMgrField.setAccessible(true);
                    //  取得mService的实例化类IConnectivityManager
                    iConMgr = iConMgrField.get(conMgr);
                    //  取得IConnectivityManager类
                    iConMgrClass = iConMgr.getClass();
                    //  取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
                    try {
                        setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
                                "setMobileDataEnabled", Boolean.TYPE);
                        //  设置setMobileDataEnabled方法可访问
                        setMobileDataEnabledMethod.setAccessible(true);
                        //  调用setMobileDataEnabled方法
                        setMobileDataEnabledMethod.invoke(iConMgr, enabled);
                    } catch (NoSuchMethodException e) {
                        setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
                                "setMobileDataEnabled", String.class, Boolean.TYPE);
                        //  设置setMobileDataEnabled方法可访问
                        setMobileDataEnabledMethod.setAccessible(true);
                        //  调用setMobileDataEnabled方法
                        setMobileDataEnabledMethod.invoke(iConMgr,
                                context.getPackageName(), enabled);
                    }


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Convert a IPv4 address from an integer to an InetAddress.
         *
         * @param hostAddress an int corresponding to the IPv4 address in network.html byte order
         * @return {@link InetAddress}
         */
        public static InetAddress intToInetAddress(int hostAddress) {
            byte[] addressBytes = {(byte) (0xff & hostAddress), //  SUPPRESS CHECKSTYLE
                    (byte) (0xff & (hostAddress >> 8)), //  SUPPRESS CHECKSTYLE
                    (byte) (0xff & (hostAddress >> 16)), //  SUPPRESS CHECKSTYLE
                    (byte) (0xff & (hostAddress >> 24))}; //  SUPPRESS CHECKSTYLE

            try {
                return InetAddress.getByAddress(addressBytes);
            } catch (UnknownHostException e) {
                throw new AssertionError();
            }
        }

        /**
         * 获取基站信息， gsm网络是cell id，cdma是base station id
         *
         * @param ctx Context
         * @return info
         */
        @TargetApi(Build.VERSION_CODES.M)
        public static String getCellInfo(Context ctx) {
            String cellInfo = null;
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                    ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                    ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                return "";
            }
            try {
                TelephonyManager teleMgr = (TelephonyManager) ctx.getSystemService(
                        Context.TELEPHONY_SERVICE);
                CellLocation cellLocation = teleMgr.getCellLocation();

                if (cellLocation instanceof GsmCellLocation) {
                    GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                    cellInfo = Integer.toString(gsmCellLocation.getCid());

                } else if (cellLocation instanceof CdmaCellLocation) {
                    CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cellLocation;
                    cellInfo = Integer.toString(cdmaCellLocation.getBaseStationId());
                }

            } catch (Exception e) {
//                if (DEBUG) {
                    Log.e("song", "getCellInfo fail!" + e.toString());
//                }
            }

            if (TextUtils.isEmpty(cellInfo)) {
                cellInfo = "";
            }

            return cellInfo;
        }

        /**
         * 获取Wifi信息，mac地址
         *
         * @param ctx Context
         * @return info
         */
        public static String getWifiMacAddress(Context ctx) {

            WifiInfo wifiInfo = getWifiInfoSafely((WifiManager) ctx.getSystemService(
                    Context.WIFI_SERVICE));
            String mac = wifiInfo == null ? "" : wifiInfo.getMacAddress();
            return mac == null ? "" : mac;
        }

        /**
         * 获取wifi的SSID
         *
         * @param context Context
         * @return wifi名字
         */
        public static String getWifiSSID(Context context) {
            try {
                Context appContext = context.getApplicationContext();
                WifiManager wifiService = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = getWifiInfoSafely(wifiService);
                if (wifiInfo != null) {
                    return wifiInfo.getSSID();
                }
            } catch (Exception e) {
//                if (DEBUG) {
                    e.printStackTrace();
//                }
                return "";
            }
            return "";
        }


        /**
         * 获取host在MetaData里面声明的类实例
         *
         * @param ctx application context
         * @param key meta-data key
         * @return 类实例
         */
        public static Object getHostMetaDataClassInstance(Context ctx, String key) {
            Object object = null;
            ApplicationInfo hostAppInfo = null;
            try {
                hostAppInfo = ctx.getPackageManager()
                        .getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e1) {
//                if (DEBUG) {
                    e1.printStackTrace();
//                }
            }

            if (hostAppInfo != null && hostAppInfo.metaData != null) {
                String clazz = hostAppInfo.metaData.getString(key);
                if (clazz != null && clazz.length() > 0) {
                    try {
                        object = Class.forName(clazz).newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            return object;
        }
    }


}