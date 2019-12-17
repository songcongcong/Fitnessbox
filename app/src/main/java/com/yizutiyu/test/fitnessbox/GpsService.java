package com.yizutiyu.test.fitnessbox;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.yizutiyu.test.fitnessbox.eventbus.GpsEventBus;
import com.yizutiyu.test.fitnessbox.sqlite.DataBaseOpenHelper;
import com.yizutiyu.test.fitnessbox.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * @author songcongcong
 * @date 2019-08-14.
 */
public class GpsService extends Service {

    /**
     * mLocationClient
     */
    public static LocationClient mLocationClient = null;

    /**
     * myListener
     */
    private MyLocationListener myListener = new MyLocationListener();
    /**
     * mSharedPreferences
     */
    private SharedPreferences mSharedPreferences;
    private List<String> list = new ArrayList<>();
    // 数据库表名
    public static String mDbTableName = "t_message";
    // 创建表
    public static String sql_message = "create table " + mDbTableName + " (id int primary key,longitude varchar(50),lat varchar(50))";

    private int mCount = 0;

    //service bind with activity.
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    //create
    @Override
    public void onCreate() {
        super.onCreate();
    }


    //start service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
//        EventBus.getDefault().register(this);
        //声明LocationClient类
        LocationClient mLocation = getInstance();
        if (!mLocation.isStarted()) { //如果当前定时未开启，则开启
            //注册监听函数
            mLocation.registerLocationListener(myListener);
            setLocation();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建单实例对象
     *
     * @return LocationClient
     */
    public LocationClient getInstance() {
        if (mLocationClient != null) {
            return mLocationClient;
        } else {
            mLocationClient = new LocationClient(getApplicationContext());
        }
        return mLocationClient;
    }

    /**
     * 开启定位
     */
    private void setLocation() {
        final LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll"); //可选，默认gcj02，设置返回的定位结果坐标系
//		option.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setScanSpan(1000); //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true); //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true); //可选，默认false,设置是否使用gps
        option.setIgnoreKillProcess(false); //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false); //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false); //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 定位监听
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mCount++;
            float radius = location.getRadius();
            // 此处设置开发者获取到的方向信息，顺时针0-360
            MyLocationData locData = new MyLocationData.Builder().accuracy(radius)
                    .latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            if (!String.valueOf(locData.longitude).equals("4.9E-324")
                    && !String.valueOf(locData.latitude).equals("4.9E-324")) {
                if (mCount >= 2) {
//                    checkWifiState(locData.longitude, locData.latitude);
//                    setNetWork(locData.longitude, locData.latitude);
//                    Log.d("song", "存储之前定位：" + locData.longitude + ":" + locData.latitude + ",:" + mCount);
                    AppUtils.requestUrl(GpsService.this, locData.longitude, locData.latitude, mSharedPreferences);
                    AppUtils.requestAddressUrl(GpsService.this, locData.longitude, locData.latitude, mSharedPreferences);
//                    list.add(sql_message);
//                    //创建存放数据的ContentValues对象
//                    ContentValues values = new ContentValues();
//                    values.put("longitude", locData.longitude);
//                    values.put("lat", locData.latitude);
//                    DataBaseOpenHelper gps = DataBaseOpenHelper.getInstance(GpsService.this, mDbTableName, 1, list);
//                    // 删除数据库里的数据
//                    gps.delete(mDbTableName, null, null);
//                    gps.insert(mDbTableName, values);
//                    Cursor cursor = gps.query(mDbTableName, new String[]{"longitude", "lat"}, null, null, null, null, null);
//                    while (cursor.moveToNext()) {
//                        String mLongitude = cursor.getString(cursor.getColumnIndex("longitude"));
//                        String lat = cursor.getString(cursor.getColumnIndex("lat"));
//                        Log.d("song", "取出定位：" + mLongitude + ":" + lat);
//                        Toast.makeText(GpsService.this, "循环---：" + mLongitude + ",:" + lat, Toast.LENGTH_LONG).show();
//                        AppUtils.requestUrl(GpsService.this, Double.valueOf(mLongitude), Double.valueOf(lat), mSharedPreferences);
//                        AppUtils.requestAddressUrl(GpsService.this, Double.valueOf(mLongitude), Double.valueOf(lat), mSharedPreferences);
//                    }
//                    cursor.close();
                }
            }
        }
    }

//	private void getPopupWindow(){
//		View view = View.inflate(this, R.layout.newtwork_layout, null);
//		final AlertDialog alertDialog = new AlertDialog.Builder(this)
//				.setView(view)
//				.create();
//		TextView  mCancle = view.findViewById(R.id.net_cancle);
//		TextView mSure = view.findViewById(R.id.net_sure);
//		mCancle.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				alertDialog.dismiss();
//			}
//		});
//		mSure.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				alertDialog.dismiss();
//				NetWorkUtils.GoSetting(GpsService.this);
//			}
//		});
//		alertDialog.show();
//	}

//    // 检测人脸接受到的File文件
//    @Subscribe
//    public void onMessageEvent(GpsEventBus gpsEventBus) {
//        if (gpsEventBus != null) {
//            Toast.makeText(GpsService.this, "实时数据成功：" + gpsEventBus.getLongide() + ",:" + gpsEventBus.getLat(), Toast.LENGTH_LONG).show();
//
//        }
//    }

    /**
     * 检查wifi强弱并更改图标显示
     */
    public void checkWifiState(double longitude, double latitude) {
        if (isWifiConnect()) {
            WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi = mWifiInfo.getRssi();//获取wifi信号强度
            if (wifi > -50 && wifi < 0) {//最强
                setNetWork(longitude, latitude);  // 走网络
                Log.d("song", "最强");
            } else if (wifi > -70 && wifi < -50) {//较强
                setNetWork(longitude, latitude); // 走网络
                Log.d("song", "较强");
            } else if (wifi > -80 && wifi < -70) {//较弱
                setSqlite(longitude, latitude);
                Log.d("song", "较弱");
            } else if (wifi > -100 && wifi < -80) {//微弱
                setSqlite(longitude, latitude);
                Log.d("song", "微弱");
            }
        } else {
            //无连接
//            Toast.makeText(this, "网络无连接，请去设置网络！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 检查wifi是否处开连接状态
     *
     * @return
     */
    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }

    private void setSqlite(double longitude, double latitude) {
        Log.d("song", "存储之前定位：" + longitude + ":" + latitude + ",:" + mCount);
//                AppUtils.requestUrl(GpsService.this, locData.longitude, locData.latitude, mSharedPreferences);
//                AppUtils.requestAddressUrl(GpsService.this, locData.longitude, locData.latitude, mSharedPreferences);
        list.add(sql_message);
        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        values.put("longitude", longitude);
        values.put("lat", latitude);
        DataBaseOpenHelper gps = DataBaseOpenHelper.getInstance(GpsService.this, mDbTableName, 1, list);
        // 删除数据库里的数据
        gps.delete(mDbTableName, null, null);
        gps.insert(mDbTableName, values);
        Cursor cursor = gps.query(mDbTableName, new String[]{"longitude", "lat"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String mLongitude = cursor.getString(cursor.getColumnIndex("longitude"));
            String lat = cursor.getString(cursor.getColumnIndex("lat"));
            Log.d("song", "取出定位：" + longitude + ":" + lat);
            AppUtils.requestUrl(GpsService.this, Double.valueOf(mLongitude), Double.valueOf(lat), mSharedPreferences);
            AppUtils.requestAddressUrl(GpsService.this, Double.valueOf(longitude), Double.valueOf(lat), mSharedPreferences);
        }
        cursor.close();
    }

    private void setNetWork(double longitude, double latitude) {
        Log.d("song", "存储之前定位：" + longitude + ":" + latitude + ",:");
        AppUtils.requestUrl(GpsService.this, Double.valueOf(longitude), Double.valueOf(latitude), mSharedPreferences);
        AppUtils.requestAddressUrl(GpsService.this, Double.valueOf(longitude), Double.valueOf(latitude), mSharedPreferences);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }
}