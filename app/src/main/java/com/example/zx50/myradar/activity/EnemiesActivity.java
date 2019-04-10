package com.example.zx50.myradar.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.example.zx50.myradar.model.Contact;
import com.example.zx50.myradar.util.EnemiesListAdapter;
import com.example.zx50.myradar.R;
import com.example.zx50.myradar.util.MyApplication;

import java.util.ArrayList;

public class EnemiesActivity extends Activity {


    private ListView enemiesList = null;
    private EnemiesListAdapter enemiesListAdapter = null;
    private Button btnAdd = null;
    private ToggleButton btnEdit = null;
    private Button btnRadar = null;
    private Button btnFriends = null;
    private ArrayList<Contact> enemiesCollection = null;
    private MyApplication myApplication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApplication = (MyApplication)getApplicationContext();
        setContentView(R.layout.enemies_list);
        //InitData();
        enemiesCollection = myApplication.getEnemiesCollection();
        enemiesList = (ListView)findViewById(R.id.lvw_enemies_list);
        enemiesListAdapter = new EnemiesListAdapter(EnemiesActivity.this, enemiesCollection);
        enemiesList.setAdapter(enemiesListAdapter);
        btnAdd = (Button)findViewById(R.id.btn_enemies_list_add);
        btnAdd.setOnClickListener(new EnemiesAddClick());
        btnEdit = (ToggleButton)findViewById(R.id.btn_enemies_list_edit);
        btnEdit.setOnClickListener(new EnemiesEditClick());
        btnRadar = (Button)findViewById(R.id.btn_enemies_list_radar);
        btnRadar.setOnClickListener(new EnemiesListRadarClick());
        btnFriends = (Button)findViewById(R.id.btn_enemies_list_friends);
        btnFriends.setOnClickListener(new EnemiesListFriendsClick());
    }

    public void InitData(){
        enemiesCollection = new ArrayList<>();
        for (int i = 0; i < 6; i++){
            Contact enemy = new Contact();
            enemy.setName("pdd");
            enemy.setNumber("1234567891");
            enemiesCollection.add(enemy);
        }
    }

    class EnemiesAddClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EnemiesActivity.this);
            View dialogview = View.inflate(EnemiesActivity.this, R.layout.dialog_add_enemy, null);
            final AlertDialog dialog = builder.create();
            dialog.setView(dialogview);
            final EditText enemyName = (EditText)dialogview.findViewById(R.id.txt_enemy_name);
            final EditText enemyNumber = (EditText)dialogview.findViewById(R.id.txt_enemy_number);
            Button dialogOk = (Button)dialogview.findViewById(R.id.btn_dialog_ok);
            dialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = enemyName.getText().toString();
                    String number = enemyNumber.getText().toString();
                    enemiesListAdapter.addData(name, number);
                    enemiesListAdapter.notifyDataSetChanged();
                    dialog.dismiss();
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
    }

    class EnemiesEditClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (enemiesListAdapter.getIsBtnVisible()){
                enemiesListAdapter.setIsBtnVisible(false);
                enemiesListAdapter.notifyDataSetChanged();
            }
            else {
                enemiesListAdapter.setIsBtnVisible(true);
                enemiesListAdapter.notifyDataSetChanged();
            }
        }
    }

    class EnemiesListRadarClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(EnemiesActivity.this, RadarActivity.class);
            startActivity(intent);
        }
    }

    class EnemiesListFriendsClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(EnemiesActivity.this, FriendsActivity.class);
            startActivity(intent);
        }
    }
}
