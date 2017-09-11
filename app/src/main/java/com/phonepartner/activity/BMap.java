package com.phonepartner.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.phonepartner.R;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.util.MessageEvent;
import com.phonepartner.util.SessionUtils;

import de.greenrobot.event.EventBus;

public class BMap extends android.support.v4.app.Fragment {
    public View rootView;
    //0为自己为中心,1为信息为中心
    public static int mode = 0;
    public static MapView mMapView = null;
    public static LocationClient mLocationClient = null;
    public static BDAbstractLocationListener myListener = new MylocationListener();
    public static BaiduMap baiduMap;

    public BMap() {
        super();
    }

    //传递过来的用户信息
    public String userInfoDetail = "显示错误，请再来一次";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        //声明LocationClient类
        mLocationClient = new LocationClient(MainActivity.context);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
        //获取传过来的参数
        Bundle bundle=getArguments();
        if(bundle!=null)
        userInfoDetail=bundle.getString("singal");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //绑定xml文件
        rootView = inflater.inflate(R.layout.activity_baidu_map, container, false);
        //绑定控件
        mMapView = (MapView) rootView.findViewById(R.id.bmapView);
        Button setCenter=(Button)rootView.findViewById(R.id.setCenter);

        baiduMap = mMapView.getMap();
        //设定按钮监听
        setCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode=0;
            }
        });
        //        建立marker点击事件监听
        BaiduMap.OnMarkerClickListener listener = new BaiduMap.OnMarkerClickListener() {
            /**
             * 地图 Marker 覆盖物点击事件监听函数
             * @param marker 被点击的 marker
             */
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() == "自己的位置") {
                    //创建InfoWindow展示的view
                    TextView info = new TextView(MainActivity.context);
                    info.setBackgroundResource(R.drawable.bg_common_toast);
                    UserInfo user = SessionUtils.getInstance(MainActivity.context.getApplicationContext()).getLoginUserInfo();
                    String device_address=null;
                    String device_name=null;
                    try{
                        device_address=user.getDevice().getAddress();
                        device_name=user.getDevice().getName();
                    }catch(Exception e){
                        device_address="还未连接设备";
                        device_name="还未连接设备";
                    }

                    String userInfoDetail_me =
                            "设备编号：" + device_address +"\n"
                                    + "设备名称：" + device_name + "\n"+
                                    "姓名：" + user.getMemoName() + "\n" +
                                    "紧急联系人" + "\n" +
                                    user.getEmergenceName1() + user.getEmergencePhone1() +
                                    "\n"+"经度:"+MylocationListener.x+"纬度:"+ MylocationListener.y ;
                    Log.d("自己的消息",userInfoDetail_me);
                    info.setText(userInfoDetail_me);
                    info.setWidth(550);
                    info.setHeight(300);
                    //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                    InfoWindow mInfoWindow = new InfoWindow(info, marker.getPosition(), -47);
                    //显示InfoWindow
                    baiduMap.showInfoWindow(mInfoWindow);
                } else {
                    //创建InfoWindow展示的view
                    TextView info = new TextView(MainActivity.context);
                    info.setBackgroundResource(R.drawable.bg_common_toast);
                    info.setWidth(550);
                    info.setHeight(300);
                    //此处增加判断

                        info.setText(userInfoDetail);

                    //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                    InfoWindow mInfoWindow = new InfoWindow(info, marker.getPosition(), -47);
                    //显示InfoWindow
                    baiduMap.showInfoWindow(mInfoWindow);
                }


                return false;
            }
        };
        //建立地图点击事件
        BaiduMap.OnMapClickListener mapClickListener = new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                baiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        };
        baiduMap.setOnMarkerClickListener(listener);
        baiduMap.setOnMapClickListener(mapClickListener);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 0;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位

        mLocationClient.setLocOption(option);
    }

    public void onEvent(MessageEvent event) {
        String message = event.getMesage();
        float jd =Float.parseFloat(message.substring(message.indexOf("度")+1,message.lastIndexOf("纬"))) ;
        float wd = Float.parseFloat(message.substring(message.indexOf(":")+1,message.length()));
        UserInfo u = event.getUserInfo();
        if (u != null) {
            String device_address="未连接设备";
            String device_name="未连接设备";
            try{
                device_address=u.getDevice().getAddress();
                        device_name=u.getDevice().getName();
            }catch (Exception e){

            }
             userInfoDetail =
                    "设备编号：" + device_address + "\n"
                            + "设备名称：" + device_name + "\n"
                            + "姓名：" + u.getMemoName() + "\n" +
                            "紧急联系人" + "\n" +
                            u.getEmergenceName1() + u.getEmergencePhone1() + "\n" +
                            event.getMesage();

            Log.i("收到了消息","消息内容为:"+userInfoDetail);


        }

    }




}
