package com.example.lbstext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private TextView positionText;

    private MapView mapView;

    private BaiduMap baiduMap;
    private Boolean isFirstLocate = true;

    public static final String TAG = "LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        positionText = (TextView) findViewById(R.id.position_text_view);
        mapView = (MapView) findViewById(R.id.bmapView);

        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        checkpermission();
    }

    public void checkpermission() {
        //单个权限请求
        List<String> permissions = new ArrayList<String>();
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.INTERNET);

        List<String> requespermissions = new ArrayList<String>();
        for (int i = 0; i < permissions.size(); i++) {
            if (ContextCompat.checkSelfPermission(this, permissions.get(i))
                    != PackageManager.PERMISSION_GRANTED) {
                requespermissions.add(permissions.get(i));
            }
        }
        if (requespermissions.size() == 0) {
            requestLocation();
            return;
        }
        String[] st = new String[requespermissions.size()];
        ActivityCompat.requestPermissions(this, requespermissions.toArray(st), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Log.i(TAG, "用户拒绝了");
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Log.d(TAG, "未知错误: ");
                    finish();
                }
                break;
            default:
                break;
        }

    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();

/*        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标*/


        option.setIsNeedAddress(true);


        option.setScanSpan(5000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

/*        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setNeedNewVersionRgc(true);
        //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true*/

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }

        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            String country = location.getCountry(); // 国家
            String province = location.getProvince();// 省份
            String city = location.getCity(); // 市
            String district = location.getDistrict(); // 区
            String street = location.getStreet(); //街道


            Log.d(TAG, "latitud = " + latitude + ", longitude " + longitude);
            Log.d(TAG, "radius = " + radius + ", coorType " + coorType);
            Log.d(TAG, "errorCode: " + errorCode);
            Log.d(TAG, "country: " + country);
            Log.d(TAG, "province: " + province);
            Log.d(TAG, "city: " + city);
            Log.d(TAG, "district: " + district);
            Log.d(TAG, "street: " + street);

            Log.d(TAG, "getStreetNumber: " + location.getStreetNumber());
            Log.d(TAG, "getLocationDescribe: " + location.getLocationDescribe());
            Log.d(TAG, "getFloor: " + location.getFloor());
            Log.d(TAG, "getBuildingID: " + location.getBuildingID());
            Log.d(TAG, "getBuildingName: " + location.getBuildingName());


            StringBuilder currentPostion = new StringBuilder();
            currentPostion.append("纬度").append(latitude).append("\n");
            currentPostion.append("经度").append(longitude).append("\n");

            currentPostion.append("国家：").append(country).append("\n");
            currentPostion.append("省：").append(province).append("\n");

            currentPostion.append("市：").append(city).append("\n");
            currentPostion.append("区：").append(district).append("\n");
            currentPostion.append("街道：").append(street).append("\n");

            currentPostion.append("定位方式： ");
            if (errorCode == BDLocation.TypeGpsLocation) {
                currentPostion.append("GPS");
                Log.d(TAG, "定位方式: " + "GPS");
                navigateTo(location);
            } else if (errorCode == BDLocation.TypeNetWorkLocation) {
                currentPostion.append("网络");
                Log.d(TAG, "定位方式: " + "网络");
                navigateTo(location);
            }


            positionText.setText(currentPostion);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}
