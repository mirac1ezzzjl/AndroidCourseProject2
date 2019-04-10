package com.example.zx50.myradar.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.zx50.myradar.R;
import com.example.zx50.myradar.model.ContactMarker;
import com.example.zx50.myradar.util.MyApplication;

import java.util.Map;

public class FriendsDetail extends AppCompatActivity {

    private double latitude = 0;
    private double longitude = 0;
    private String address = null;
    private Button btnList = null;
    private ToggleButton btnEdit = null;
    private Button btnDelete = null;
    private TextView friendName = null;
    private TextView friendNumber = null;
    private TextView friendLatlng = null;
    private TextView friendAccuracy = null;
    private TextView friendNearAddress = null;
    private TextView secsLast = null;
    private TextView secsNext = null;
    private Button btnRadar = null;
    private Button btnEnemies = null;
    private boolean isBtnVisible = false;
    private MyApplication myApplication = null;
    private GeoCoder geoCoder = GeoCoder.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myApplication = (MyApplication)getApplicationContext();
        setContentView(R.layout.friend_detail);
        Intent intent = getIntent();
        final String phoneNumber = intent.getStringExtra("number");
        //String location = intent.getStringExtra("latNlon");
        //StringTokenizer st = new StringTokenizer(location, "/") ;
        //latitude = Double.parseDouble(st.nextToken());
        //longitude = Double.parseDouble(st.nextToken());
        latitude = myApplication.getFriend(phoneNumber).getLatitude();
        longitude = myApplication.getFriend(phoneNumber).getLongitude();
        btnList = (Button)findViewById(R.id.btn_friends_list);
        btnEdit = (ToggleButton)findViewById(R.id.btn_friends_list_edit);
        btnDelete = (Button)findViewById(R.id.btn_delete);
        btnDelete.setVisibility(View.GONE);
        btnRadar = (Button)findViewById(R.id.btn_radar);
        btnEnemies = (Button)findViewById(R.id.btn_enemies);
        friendName = (TextView)findViewById(R.id.txt_friend_name);
        String name = myApplication.getFriend(phoneNumber).getName();
        friendName.setText(name);
        friendNumber = (TextView)findViewById(R.id.txt_friend_number);
        friendLatlng = (TextView)findViewById(R.id.txt_friend_long_lang);
        friendAccuracy = (TextView)findViewById(R.id.txt_friend_accuracy);
        friendAccuracy.setText("<10m");
        String latlon = latitude + "/" + longitude;
        friendNumber.setText(phoneNumber);
        friendLatlng.setText(latlon);
        reverseGeoParse(latitude, longitude, new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    //Toast.makeText(FriendsDetail.this, "抱歉，未能找到结果!", Toast.LENGTH_LONG);
                    friendNearAddress = (TextView)findViewById(R.id.txt_friend_nearest_city);
                    friendNearAddress.setText("NO RESULT");
                }else{////得到结果后处理方法
                    //Toast.makeText(FriendsDetail.this, "地址为："+result.getAddress(), Toast.LENGTH_LONG);
                    friendNearAddress = (TextView)findViewById(R.id.txt_friend_nearest_city);
                    address = result.getAddress();
                    friendNearAddress.setText(address);
                }
            }
        });
        //friendNearAddress.setText(address);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsDetail.this, FriendsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBtnVisible){
                    btnDelete.setVisibility(View.VISIBLE);
                    isBtnVisible = true;
                }
                else {
                    btnDelete.setVisibility(View.GONE);
                    isBtnVisible = false;
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsDetail.this);
                View dialogview = View.inflate(FriendsDetail.this, R.layout.dialog_delete, null);
                final AlertDialog dialog = builder.create();
                dialog.setView(dialogview);
                TextView numberlabel = (TextView)dialogview.findViewById(R.id.txt_friend_number_label);
                TextView number = (TextView)dialogview.findViewById(R.id.txt_friend_number);
                number.setText(phoneNumber);
                Button dialogOk = (Button)dialogview.findViewById(R.id.btn_dialog_ok);
                dialogOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myApplication.delFriend(phoneNumber);
                        Map<String, ContactMarker> friendsMarker = myApplication.getFriendMarkers();
                        Marker marker = friendsMarker.get(phoneNumber).getMarker();
                        Polyline line = friendsMarker.get(phoneNumber).getLine();
                        Marker distanceMarker = friendsMarker.get(phoneNumber).getDistanceMarker();
                        if (marker != null){
                            marker.remove();
                            line.remove();
                            distanceMarker.remove();
                        }
                        dialog.dismiss();
                        finish();
                    }
                });
                Button dialogClose = (Button)dialogview.findViewById(R.id.btn_dialog_close);
                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btnRadar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsDetail.this, RadarActivity.class);
                startActivity(intent);
            }
        });

        btnEnemies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsDetail.this, EnemiesActivity.class);
                startActivity(intent);
            }
        });
    }

    void reverseGeoParse(double lat, double lon,
                         OnGetGeoCoderResultListener listener) {
        geoCoder.setOnGetGeoCodeResultListener(listener);
        LatLng pt1 = new LatLng(lat, lon);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(pt1));
    }
}
