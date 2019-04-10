package com.example.zx50.myradar.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.zx50.myradar.model.Contact;
import com.example.zx50.myradar.R;
import com.example.zx50.myradar.model.ContactMarker;
import com.example.zx50.myradar.mywidget.MyTextView2;
import com.example.zx50.myradar.util.AESencryptor;
import com.example.zx50.myradar.util.MyApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class RadarActivity extends AppCompatActivity {

    private MyApplication myApplication;
    private double latitude;
    private double longitude;
    public LocationClient mLocationClient;
    public MyLocationListener myListener = new MyLocationListener();
    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private boolean isFirstLocation = true;

    private ArrayList<Contact> friendsCollection = null;
    private ArrayList<Contact> enemiesCollection = null;

    private Button btnLocate = null;
    private ToggleButton btnRefresh = null;
    private Button btnFriends = null;
    private Button btnEnemies = null;

    private Marker myLocMarker = null;
    private Map<String, ContactMarker> friendMarkers = null;
    private Map<String, ContactMarker> enemyMarkers = null;

    private MyReceiver myReceiver = null;
    public static String ACTION_INTENT_RECEIVER = "com.gc.broadcast.receiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        //SDKInitializer.initialize(getApplicationContext());
        myApplication = (MyApplication)getApplicationContext();
        setContentView(R.layout.main);
        friendsCollection = myApplication.getFriendsCollection();
        enemiesCollection = myApplication.getEnemiesCollection();
        btnFriends = (Button)findViewById(R.id.btn_friends);
        btnFriends.setOnClickListener(new FriendsClick());
        btnEnemies = (Button)findViewById(R.id.btn_enemies);
        btnEnemies.setOnClickListener(new EnemiesClick());
        friendMarkers = new HashMap<>();
        enemyMarkers = new HashMap<>();
        mMapView = (MapView)findViewById(R.id.mapView);
        baiduMap = mMapView.getMap();
        myApplication.setBaiduMap(baiduMap);
        myApplication.setLatitude(latitude);
        myApplication.setLongitude(longitude);
        setMarker();
        setUserMapCenter();
        //InitData();
        startLocate();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String phoneNumber = marker.getExtraInfo().getString("number");
                if (phoneNumber != null){
                    if (marker.getExtraInfo().getBoolean("isFriend")){
                        Intent intent = new Intent(RadarActivity.this, FriendsDetail.class);
                        intent.putExtra("number", phoneNumber);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(RadarActivity.this, EnemiesDetail.class);
                        intent.putExtra("number", phoneNumber);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
        btnRefresh = (ToggleButton)findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Contact i : friendsCollection){
                    String phoneNumber = i.getNumber();
                    String smsContent = "where are you?";
                    String content = "";
                    try{
                        //content = AESencryptor.encrypt(smsContent, myApplication.getKey());
                        content = AESencryptor.encrypt(smsContent, "123");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> list = smsManager.divideMessage(content);
                    for (String text : list){
                        smsManager.sendTextMessage(phoneNumber, null, text, null, null);
                    }
                }
            }
        });
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_RECEIVER);
        registerReceiver(myReceiver, filter);
    }

    public void InitData(){
        Contact friend1 = new Contact();
        friend1.setName("me");
        friend1.setNumber("13128531850");
        friend1.setLatitude(22.261365);
        friend1.setLongitude(113.532989);
        friendsCollection.add(friend1);
        Contact enemy1 = new Contact();
        enemy1.setName("xiaohong");
        enemy1.setNumber("4567123");
        enemy1.setLatitude(22.251953);
        enemy1.setLongitude(113.526421);
        enemiesCollection.add(enemy1);
    }

    public void setMarker(){

        Log.v("pcw","setMarker : lat : "+ latitude+" lon : " + longitude);
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        Bundle extraMsg = new Bundle();
        extraMsg.putString("number", null);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker);
        //构建MarkerOption，用于在地图上添加Marker
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .extraInfo(extraMsg)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        myLocMarker = (Marker)baiduMap.addOverlay(option);
        myLocMarker.setAnchor(0.5f, 0.5f);
    }

    private void setUserMapCenter() {
        Log.v("pcw","setUserMapCenter : lat : "+ latitude+" lon : " + longitude);
        LatLng cenpt = new LatLng(latitude,longitude);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);

    }

    private void setFriendsMarker() {
        LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendsCollection = myApplication.getFriendsCollection();
        View markerView = null;
        LatLng mypoint = new LatLng(latitude, longitude);
        Polyline line = null;
        Bundle extraMsg = null;
        double distance = 0;
        double centerLat = 0;
        double centerLon = 0;
        LatLng centerpoint;
        for (Contact friend : friendsCollection){
            LatLng point = new LatLng(friend.getLatitude(), friend.getLongitude());
            extraMsg = new Bundle();
            extraMsg.putString("number", friend.getNumber());
            extraMsg.putString("latNlon", friend.getLatitude()+"/"+friend.getLongitude());
            extraMsg.putBoolean("isFriend", true);
            //图标绘制
//            BitmapDescriptor bitmap = BitmapDescriptorFactory
//                    .fromResource(R.drawable.friend_marker);
//            OverlayOptions option = new MarkerOptions()
//                    .position(point)
//                    .extraInfo(extraMsg)
//                    .icon(bitmap);
//            Marker marker = (Marker)baiduMap.addOverlay(option);
            markerView = inflater.inflate(R.layout.friend_marker, null);
            MyTextView2 friendName = (MyTextView2) markerView.findViewById(R.id.friend_marker_name);
            friendName.setText(friend.getName());
            friendName.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(),"04B_03B_.TTF"));
            MyTextView2 friendNumber = (MyTextView2) markerView.findViewById(R.id.friend_marker_number);
            friendNumber.setText(friend.getNumber());
            friendNumber.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(), "04B_03B_.TTF"));
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(markerView);
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .extraInfo(extraMsg)
                    .icon(bitmap);
            Marker marker = (Marker)baiduMap.addOverlay(option);
            //线绘制
            List<LatLng> points = new ArrayList<LatLng>();
            points.add(mypoint);
            points.add(point);
            OverlayOptions ooPolyline = new PolylineOptions()
                    .width(6)
                    .color(ContextCompat.getColor(getBaseContext(),R.color.buttonGreen))
                    .points(points);
            line = (Polyline)baiduMap.addOverlay(ooPolyline);
            //距离标识
            distance = DistanceUtil.getDistance(mypoint,point)/1000;
            BigDecimal shortDistance = new BigDecimal(distance).setScale(2,BigDecimal.ROUND_HALF_UP);
            centerLat = (latitude + friend.getLatitude())/2;
            centerLon = (longitude + friend.getLongitude())/2;
            centerpoint = new LatLng(centerLat, centerLon);
            markerView = inflater.inflate(R.layout.friend_distance_marker, null);
            MyTextView2 distanceText = (MyTextView2)markerView.findViewById(R.id.friend_distance_marker);
            distanceText.setText(shortDistance.toString()+"km");
            distanceText.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(),"04B_03B_.TTF"));
            BitmapDescriptor bitmap2 = BitmapDescriptorFactory.fromView(markerView);
//            OverlayOptions textOption = new TextOptions()
//                    .fontSize(50)
//                    .typeface(Typeface.createFromAsset(getBaseContext().getAssets(),"04B_03B_.TTF"))
//                    .fontColor(0xFF39B54A)
//                    .text(shortDistance.toString()+"km")
//                    .position(centerpoint);
            OverlayOptions textOption = new MarkerOptions()
                        .position(centerpoint)
                        .icon(bitmap2);
            Marker distanceMarker = (Marker)baiduMap.addOverlay(textOption);

            ContactMarker contactMarker = new ContactMarker();
            contactMarker.setMarker(marker);
            contactMarker.setLine(line);
            contactMarker.setDistanceMarker(distanceMarker);
            friendMarkers.put(friend.getNumber(), contactMarker);

        }
        myApplication.setFriendMarkers(friendMarkers);
    }

    private void setEnemiesMarker() {
        LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        enemiesCollection = myApplication.getEnemiesCollection();
        View markerView = null;
        ArrayList<LatLng> enemiesLoc = new ArrayList<>();
        LatLng mypoint = new LatLng(latitude, longitude);
        Polyline line = null;
        Bundle extraMsg = null;
        double distance = 0;
        double centerLat = 0;
        double centerLon = 0;
        LatLng centerpoint;
        for (Contact enemy : enemiesCollection){
            LatLng point = new LatLng(enemy.getLatitude(), enemy.getLongitude());
            enemiesLoc.add(point);
            extraMsg = new Bundle();
            extraMsg.putString("number", enemy.getNumber());
            extraMsg.putString("latNlon", enemy.getLatitude()+"/"+enemy.getLongitude());
            extraMsg.putBoolean("isFriend", false);
            //图标绘制
//            BitmapDescriptor bitmap = BitmapDescriptorFactory
//                    .fromResource(R.drawable.enemy_marker);
//            OverlayOptions option = new MarkerOptions()
//                    .position(point)
//                    .extraInfo(extraMsg)
//                    .icon(bitmap);
//            Marker marker = (Marker)baiduMap.addOverlay(option);
            markerView = inflater.inflate(R.layout.enemy_marker, null);
            MyTextView2 enemyName = (MyTextView2) markerView.findViewById(R.id.enemy_marker_name);
            enemyName.setText(enemy.getName());
            enemyName.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(),"04B_03B_.TTF"));
            MyTextView2 enemyNumber = (MyTextView2)markerView.findViewById(R.id.enemy_marker_number);
            enemyNumber.setText(enemy.getNumber());
            enemyNumber.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(), "04B_03B_.TTF"));
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(markerView);
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .extraInfo(extraMsg)
                    .icon(bitmap);
            Marker marker = (Marker)baiduMap.addOverlay(option);
            //距离线绘制
            List<LatLng> points = new ArrayList<LatLng>();
            points.add(mypoint);
            points.add(point);
            OverlayOptions ooPolyline = new PolylineOptions()
                    .width(6)
                    .color(ContextCompat.getColor(getBaseContext(),R.color.buttonRed))
                    .points(points);
            line = (Polyline)baiduMap.addOverlay(ooPolyline);
            //距离标识
            distance = DistanceUtil.getDistance(mypoint,point)/1000;
            BigDecimal shortDistance = new BigDecimal(distance).setScale(2,BigDecimal.ROUND_HALF_UP);
            centerLat = (latitude + enemy.getLatitude())/2;
            centerLon = (longitude + enemy.getLongitude())/2;
            centerpoint = new LatLng(centerLat, centerLon);
            markerView = inflater.inflate(R.layout.enemy_distance_marker, null);
            MyTextView2 distanceText = (MyTextView2)markerView.findViewById(R.id.enemy_distance_marker);
            distanceText.setText(shortDistance.toString()+"km");
            distanceText.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(),"04B_03B_.TTF"));
            BitmapDescriptor bitmap2 = BitmapDescriptorFactory.fromView(markerView);
//            OverlayOptions textOption = new TextOptions()
//                    .fontSize(50)
//                    .typeface(Typeface.createFromAsset(getAssets(),"04B_03B_.TTF"))
//                    .fontColor(0xFFEC3239)
//                    .text(shortDistance.toString()+"km")
//                    .position(centerpoint);
            OverlayOptions textOption = new MarkerOptions()
                        .position(centerpoint)
                        .icon(bitmap2);
            Marker distanceMarker = (Marker)baiduMap.addOverlay(textOption);

            ContactMarker contactMarker = new ContactMarker();
            contactMarker.setMarker(marker);
            contactMarker.setLine(line);
            contactMarker.setDistanceMarker(distanceMarker);
            enemyMarkers.put(enemy.getNumber(), contactMarker);

        }
        myApplication.setEnemyMarkers(enemyMarkers);
    }

    private void startLocate(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }



    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            float radius = bdLocation.getRadius();
            String coorType = bdLocation.getCoorType();
            int errorCode = bdLocation.getLocType();
            StringBuilder sb=new StringBuilder();
            sb.append("纬度：").append(latitude).append("\n");
            sb.append("经度：").append(longitude).append("\n");
            sb.append("定位方式：");
            if (errorCode==BDLocation.TypeGpsLocation){
                sb.append("GPS");
            }else if (errorCode==BDLocation.TypeNetWorkLocation){
                sb.append("网络");
            }
            else {
                switch (errorCode){
                    case 62:
                        sb.append("无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位");
                        break;
                    case 63:
                        sb.append("网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位");
                        break;
                    case 66:
                        sb.append("离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果");
                        break;
                    case 67:
                        sb.append("离线定位失败");
                        break;
                    case 162:
                        sb.append("请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件");
                        break;
                    case 167:
                        sb.append("服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位");
                        break;
                    case 505:
                        sb.append("AK不存在或者非法，请按照说明文档重新申请AK");
                        break;
                }
            }
            Log.e("描述：",sb.toString());

            //这个判断是为了防止每次定位都重新设置中心点和marker
            if(isFirstLocation){
                isFirstLocation = false;
                myApplication.setLatitude(bdLocation.getLatitude());
                myApplication.setLongitude(bdLocation.getLongitude());
                setFriendsMarker();
                setEnemiesMarker();
                myLocMarker.setPosition(new LatLng(myApplication.getLatitude(), myApplication.getLongitude()));
                myLocMarker.setAnchor(0.5f, 0.5f);
                //setMarker();
                setUserMapCenter();
            }
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_INTENT_RECEIVER)){
                String number = intent.getStringExtra("phoneNumber");
                String location = intent.getStringExtra("location");
                ContactMarker contactMarker = null;
                if (number!=null && location!=null){
                    StringTokenizer st = new StringTokenizer(location, "/") ;
                    double lat = Double.parseDouble(st.nextToken());
                    double lon = Double.parseDouble(st.nextToken());
                    myApplication.getFriend(number).setLatitude(lat);
                    myApplication.getFriend(number).setLongitude(lon);
                    Marker marker = null;
                    Polyline line = null;
                    Marker distanceMarker = null;
                    List<LatLng> points = new ArrayList<LatLng>();
                    LatLng myLocPoint = new LatLng(myApplication.getLatitude(), myApplication.getLongitude());
                    points.add(myLocPoint);
                    LatLng point = new LatLng(lat, lon);
                    points.add(point);
                    Double distance = DistanceUtil.getDistance(myLocPoint,point)/1000;
                    BigDecimal shortDistance = new BigDecimal(distance).setScale(2,BigDecimal.ROUND_HALF_UP);
                    Double centerLat = (myApplication.getLatitude() + lat)/2;
                    Double centerLon = (myApplication.getLongitude() + lon)/2;
                    LatLng centerPoint = new LatLng(centerLat, centerLon);
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View distanceMarkerView = inflater.inflate(R.layout.friend_distance_marker, null);
                    MyTextView2 distanceText = (MyTextView2)distanceMarkerView.findViewById(R.id.friend_distance_marker);
                    distanceText.setText(shortDistance.toString()+"km");
                    distanceText.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(),"04B_03B_.TTF"));
                    BitmapDescriptor distanceBitmap = BitmapDescriptorFactory.fromView(distanceMarkerView);
                    if (friendMarkers.get(number) == null)
                        contactMarker = friendMarkers.get(number.substring(3));
                    else
                        contactMarker = friendMarkers.get(number);
                    if (contactMarker != null){
                        marker = contactMarker.getMarker();
                        line = contactMarker.getLine();
                        distanceMarker = contactMarker.getDistanceMarker();
                        marker.setPosition(point);
                        line.setPoints(points);
                        distanceMarker.setPosition(centerPoint);
                        distanceMarker.setIcon(distanceBitmap);
                    }
                    else{
                        //图标绘制
                        Bundle extraMsg = null;
                        extraMsg = new Bundle();
                        extraMsg.putString("number", number);
                        extraMsg.putString("latNlon", location);
                        extraMsg.putBoolean("isFriend", true);
                        View markerView = null;
                        markerView = inflater.inflate(R.layout.friend_marker, null);
                        MyTextView2 friendName = (MyTextView2) markerView.findViewById(R.id.friend_marker_name);
                        friendName.setText(myApplication.getFriend(number).getName());
                        friendName.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(),"04B_03B_.TTF"));
                        MyTextView2 friendNumber = (MyTextView2) markerView.findViewById(R.id.friend_marker_number);
                        friendNumber.setText(number);
                        friendNumber.setTypeface(Typeface.createFromAsset(getBaseContext().getAssets(), "04B_03B_.TTF"));
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(markerView);
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .extraInfo(extraMsg)
                                .icon(bitmap);
                        marker = (Marker)baiduMap.addOverlay(option);
                        //线绘制
                        OverlayOptions ooPolyline = new PolylineOptions()
                                .width(6)
                                .color(ContextCompat.getColor(getBaseContext(),R.color.buttonGreen))
                                .points(points);
                        line = (Polyline)baiduMap.addOverlay(ooPolyline);
                        //距离标识绘制
                        OverlayOptions textOption = new MarkerOptions()
                                .position(centerPoint)
                                .icon(distanceBitmap);
                        distanceMarker = (Marker)baiduMap.addOverlay(textOption);

                        contactMarker = new ContactMarker();
                        contactMarker.setMarker(marker);
                        contactMarker.setLine(line);
                        contactMarker.setDistanceMarker(distanceMarker);
                        friendMarkers.put(number, contactMarker);
                        myApplication.setFriendMarkers(friendMarkers);
                    }

                }
            }
        }
    }


    class FriendsClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(RadarActivity.this, FriendsActivity.class);
            startActivity(intent);
        }
    }

    class EnemiesClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(RadarActivity.this, EnemiesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        unregisterReceiver(myReceiver);
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

}
