package com.example.zx50.myradar.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.zx50.myradar.R;
import com.example.zx50.myradar.model.Contact;
import com.example.zx50.myradar.util.MyApplication;

import java.util.ArrayList;

public class EnemiesDetail extends AppCompatActivity {

    private String address = null;
    private Button btnList = null;
    private ToggleButton btnEdit = null;
    private Button btnDelete = null;
    private TextView enemyName = null;
    private TextView enemyNumber = null;
    private TextView enemyLatlng = null;
    private TextView enemyAccuracy = null;
    private TextView enemyNearAddress = null;
    private TextView secsLast = null;
    private TextView secsNext = null;
    private Button btnRadar = null;
    private Button btnFriends = null;
    private ArrayList<Contact> enemiesCollection = null;
    private MyApplication myApplication = null;
    private boolean isBtnVisible = false;
    private GeoCoder geoCoder = GeoCoder.newInstance();
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myApplication = (MyApplication)getApplicationContext();
        setContentView(R.layout.enemy_detail);
        enemiesCollection = myApplication.getEnemiesCollection();
        Intent intent = getIntent();
        final String phoneNumber = intent.getExtras().getString("number");
        latitude = myApplication.getEnemy(phoneNumber).getLatitude();
        longitude = myApplication.getEnemy(phoneNumber).getLongitude();
        btnList = (Button)findViewById(R.id.btn_enemies_list);
        btnEdit = (ToggleButton)findViewById(R.id.btn_enemies_list_edit);
        btnDelete = (Button)findViewById(R.id.btn_delete);
        btnDelete.setVisibility(View.GONE);
        btnRadar = (Button)findViewById(R.id.btn_radar);

        btnFriends = (Button)findViewById(R.id.btn_friends);
        String name = myApplication.getEnemy(phoneNumber).getName();
        enemyName = (TextView)findViewById(R.id.txt_enemy_name);
        enemyName.setText(name);
        enemyNumber = (TextView)findViewById(R.id.txt_enemy_number);
        enemyLatlng = (TextView)findViewById(R.id.txt_enemy_long_lang);
        enemyNumber.setText(intent.getExtras().getString("number"));
        enemyAccuracy = (TextView)findViewById(R.id.txt_enemy_accuracy);
        enemyAccuracy.setText("<10m");
        String latlon = latitude + "/" + longitude;
        enemyNumber.setText(phoneNumber);
        enemyLatlng.setText(latlon);
        reverseGeoParse(latitude, longitude, new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    //Toast.makeText(FriendsDetail.this, "抱歉，未能找到结果!", Toast.LENGTH_LONG);
                    enemyNearAddress = (TextView)findViewById(R.id.txt_enemy_nearest_city);
                    enemyNearAddress.setText("NO RESULT");
                }else{////得到结果后处理方法
                    //Toast.makeText(FriendsDetail.this, "地址为："+result.getAddress(), Toast.LENGTH_LONG);
                    enemyNearAddress = (TextView)findViewById(R.id.txt_enemy_nearest_city);
                    address = result.getAddress();
                    enemyNearAddress.setText(address);
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnemiesDetail.this, EnemiesActivity.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(EnemiesDetail.this);
                View dialogview = View.inflate(EnemiesDetail.this, R.layout.dialog_delete, null);
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
                Intent intent = new Intent();
                intent.setClass(EnemiesDetail.this, RadarActivity.class);
                startActivity(intent);
            }
        });

        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(EnemiesDetail.this, FriendsActivity.class);
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
