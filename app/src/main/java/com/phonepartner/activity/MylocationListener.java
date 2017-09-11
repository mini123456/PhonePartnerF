package com.phonepartner.activity;

import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.phonepartner.R;

/**
 * Created by asus on 2017.08.28.
 */

public class MylocationListener extends BDAbstractLocationListener {
    Marker marker=null;
    public  static LatLng latLng=null;
    public static double x=0;
    public static double y=0;
    public double oldx=0;
    public double oldy=0;
    public boolean Error=false;
    @Override
    public void onReceiveLocation(BDLocation location) {

        //获取定位结果
//        location.getTime();    //获取定位时间
//        location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
//        location.getLocType();    //获取定位类型


//        location.getRadius();    //获取定位精准度
//        location.getAddrStr();    //获取地址信息
//        location.getCountry();    //获取国家信息
//        location.getCountryCode();    //获取国家码
//        location.getCity();    //获取城市信息
//        location.getCityCode();    //获取城市码
//        location.getDistrict();    //获取区县信息
//        location.getStreet();    //获取街道信息
//        location.getStreetNumber();    //获取街道码
//        location.getLocationDescribe();    //获取当前位置描述信息
//        location.getPoiList();    //获取当前位置周边POI信息
//
//        location.getBuildingID();    //室内精准定位下，获取楼宇ID
//        location.getBuildingName();    //室内精准定位下，获取楼宇名称
//        location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息

        if (location.getLocType() == BDLocation.TypeGpsLocation){

            //当前为GPS定位结果，可获取以下信息
//            location.getSpeed();    //获取当前速度，单位：公里每小时
//            location.getSatelliteNumber();    //获取当前卫星数
//            location.getAltitude();    //获取海拔高度信息，单位米
//            location.getDirection();    //获取方向信息，单位度

            x=location.getLatitude();    //获取纬度信息
            y=location.getLongitude();    //获取经度信息
            BitmapDescriptor bitmap;
            MarkerOptions markerOptions;
            //此处去除所有的marker，防止多次出现多个图标
            if(marker==null){
                oldx= x;
                oldy=y;
                //此处为增加图标
                latLng=new LatLng(x,y);
                LatLng latLng = MylocationListener.latLng;
                //准备 marker 的图片
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                //准备 marker option 添加 marker 使用
                markerOptions = new MarkerOptions().icon(bitmap).position(latLng);
                //获取添加的 marker 这样便于后续的操作
                marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
                marker.getId();
                marker.setTitle("自己的位置");
            }else {
                double distance = Math.sqrt((x - oldx) + (y - oldy));
                if(distance>50) {
                    //此处为增加图标
                    latLng = new LatLng(x, y);
                    LatLng latLng = MylocationListener.latLng;
                    //准备 marker 的图片
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                    //准备 marker option 添加 marker 使用
                    markerOptions = new MarkerOptions().icon(bitmap).position(latLng);
                    //获取添加的 marker 这样便于后续的操作
                    marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
                    marker.getId();
                    marker.setTitle("自己的位置");
                    oldx=x;
                    oldy=y;
                }
            }

            //设置中心点
            if(BMap.mode==0) {
                BMap.mode=2;
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(latLng)
                        .zoom(23)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                BMap.baiduMap.setMapStatus(mMapStatusUpdate);
            }else if(BMap.mode==1){
                BMap.mode=2;
                LatLng position = new LatLng(0, 0);
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(position)

                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                BMap.baiduMap.setMapStatus(mMapStatusUpdate);

                //准备 marker 的图片
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                //准备 marker option 添加 marker 使用
                markerOptions = new MarkerOptions().icon(bitmap).position(position);
                //获取添加的 marker 这样便于后续的操作
                Marker marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
            }



        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

            //当前为网络定位结果，可获取以下信息
//            location.getOperators();    //获取运营商信息
            Log.d("网络地图定位信息","当前为网络定位结果");
            x=location.getLatitude();    //获取纬度信息
            y=location.getLongitude();    //获取经度信息
            BitmapDescriptor bitmap;
            MarkerOptions markerOptions;
            //此处去除所有的marker，防止多次出现多个图标
            if(marker==null){
                oldx= x;
                oldy=y;
                //此处为增加图标
                latLng=new LatLng(x,y);
                LatLng latLng = MylocationListener.latLng;
                //准备 marker 的图片
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                //准备 marker option 添加 marker 使用
                markerOptions = new MarkerOptions().icon(bitmap).position(latLng);
                //获取添加的 marker 这样便于后续的操作
                marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
                marker.getId();
                marker.setTitle("自己的位置");
            }else {
                double distance = Math.sqrt((x - oldx) + (y - oldy));
                if(distance>50) {
                    //此处为增加图标
                    latLng = new LatLng(x, y);
                    LatLng latLng = MylocationListener.latLng;
                    //准备 marker 的图片
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                    //准备 marker option 添加 marker 使用
                    markerOptions = new MarkerOptions().icon(bitmap).position(latLng);
                    //获取添加的 marker 这样便于后续的操作
                    marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
                    marker.getId();
                    marker.setTitle("自己的位置");
                    oldx=x;
                    oldy=y;
                }
            }

            //设置中心点
            if(BMap.mode==0) {
                BMap.mode=2;
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(latLng)
                        .zoom(23)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                BMap.baiduMap.setMapStatus(mMapStatusUpdate);
            }else if(BMap.mode==1){
                BMap.mode=2;
                LatLng position = new LatLng(0, 0);
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(position)

                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                BMap.baiduMap.setMapStatus(mMapStatusUpdate);

                //准备 marker 的图片
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                //准备 marker option 添加 marker 使用
                markerOptions = new MarkerOptions().icon(bitmap).position(position);
                //获取添加的 marker 这样便于后续的操作
                Marker marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
            }


        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

            //当前为网络定位结果
            Log.d("离线地图定位信息","当前为网络定位结果");
            x=location.getLatitude();    //获取纬度信息
            y=location.getLongitude();    //获取经度信息
            BitmapDescriptor bitmap;
            MarkerOptions markerOptions;
            //此处去除所有的marker，防止多次出现多个图标
            if(marker==null){
                oldx= x;
                oldy=y;
                //此处为增加图标
                latLng=new LatLng(x,y);
                LatLng latLng = MylocationListener.latLng;
                //准备 marker 的图片
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                //准备 marker option 添加 marker 使用
                markerOptions = new MarkerOptions().icon(bitmap).position(latLng);
                //获取添加的 marker 这样便于后续的操作
                marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
                marker.getId();
                marker.setTitle("自己的位置");
            }else {
                double distance = Math.sqrt((x - oldx) + (y - oldy));
                if(distance>50) {
                    //此处为增加图标
                    latLng = new LatLng(x, y);
                    LatLng latLng = MylocationListener.latLng;
                    //准备 marker 的图片
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                    //准备 marker option 添加 marker 使用
                    markerOptions = new MarkerOptions().icon(bitmap).position(latLng);
                    //获取添加的 marker 这样便于后续的操作
                    marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
                    marker.getId();
                    marker.setTitle("自己的位置");
                    oldx=x;
                    oldy=y;
                }
            }

            //设置中心点
            if(BMap.mode==0) {
                BMap.mode=2;
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(latLng)
                        .zoom(23)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                BMap.baiduMap.setMapStatus(mMapStatusUpdate);
            }else if(BMap.mode==1){
                BMap.mode=2;
                LatLng position = new LatLng(0, 0);
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(position)

                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                BMap.baiduMap.setMapStatus(mMapStatusUpdate);

                //准备 marker 的图片
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_6);
                //准备 marker option 添加 marker 使用
                markerOptions = new MarkerOptions().icon(bitmap).position(position);
                //获取添加的 marker 这样便于后续的操作
                Marker marker = (Marker) BMap.baiduMap.addOverlay(markerOptions);
            }


        } else if (location.getLocType() == BDLocation.TypeServerError) {

            //当前网络定位失败
            //可将定位唯一ID、IMEI、定位失败时间反馈至loc-bugs@baidu.com
            Log.d("地图定位信息","网络定位失败");

        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

            //当前网络不通
            Log.d("地图定位信息","网络不通");

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            //当前缺少定位依据，可能是用户没有授权，建议弹出提示框让用户开启权限
            //可进一步参考onLocDiagnosticMessage中的错误返回码
            Log.d("地图定位信息","无定位权限");

        }
        Log.e("回调信息","我确实有在定位");
    }

    /**
     * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
     * 自动回调，相同的diagnosticType只会回调一次
     *
     * @param locType           当前定位类型
     * @param diagnosticType    诊断类型（1~9）
     * @param diagnosticMessage 具体的诊断信息释义
     */
    public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {

        if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {

            //建议打开GPS
            Toast.makeText(MainActivity.context,"请打开Gps",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {

            //建议打开wifi，不必连接，这样有助于提高网络定位精度！
            Toast.makeText(MainActivity.context,"请打开wifi,并不需要连接",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_LOC_PERMISSION) {

            //定位权限受限，建议提示用户授予APP定位权限！
            Toast.makeText(MainActivity.context,"定位权限受限",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_NET) {

            //网络异常造成定位失败，建议用户确认网络状态是否异常！
            Toast.makeText(MainActivity.context,"网络异常",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CLOSE_FLYMODE) {

            //手机飞行模式造成定位失败，建议用户关闭飞行模式后再重试定位！
            Toast.makeText(MainActivity.context,"请关闭飞行模式",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_INSERT_SIMCARD_OR_OPEN_WIFI) {

            //无法获取任何定位依据，建议用户打开wifi或者插入sim卡重试！
            Toast.makeText(MainActivity.context,"请打开wifi或则插入SIM卡重试",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_OPEN_PHONE_LOC_SWITCH) {

            //无法获取有效定位依据，建议用户打开手机设置里的定位开关后重试！
            Toast.makeText(MainActivity.context,"请将手机设置的定位开关后重试",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_SERVER_FAIL) {

            //百度定位服务端定位失败
            //建议反馈location.getLocationID()和大体定位时间到loc-bugs@baidu.com
            Toast.makeText(MainActivity.context,"定位服务失败",Toast.LENGTH_SHORT).show();

        } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_FAIL_UNKNOWN) {

            //无法获取有效定位依据，但无法确定具体原因
            //建议检查是否有安全软件屏蔽相关定位权限
            //或调用LocationClient.restart()重新启动后重试！
            Toast.makeText(MainActivity.context,"定位服务失败",Toast.LENGTH_SHORT).show();

        }
    }

}
