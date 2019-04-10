package com.example.zx50.myradar.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.example.zx50.myradar.util.FriendsListAdapter;
import com.example.zx50.myradar.model.Contact;
import com.example.zx50.myradar.R;
import com.example.zx50.myradar.util.MyApplication;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private ListView listFriends = null;
    private FriendsListAdapter friendsListAdapter = null;
    private Button btnAdd = null;
    private ToggleButton btnEdit = null;
    private Button btnRadar = null;
    private Button btnEnemies = null;
    private ArrayList<Contact> friendsCollection = null;
    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApplication = (MyApplication)getApplicationContext();
        setContentView(R.layout.friends_list);
        //InitData();
        friendsCollection = myApplication.getFriendsCollection();
        listFriends = (ListView)findViewById(R.id.lvw_friends_list);
        friendsListAdapter = new FriendsListAdapter(FriendsActivity.this, friendsCollection);
        listFriends.setAdapter(friendsListAdapter);
        btnAdd = (Button)findViewById(R.id.btn_friends_list_add);
        btnAdd.setOnClickListener(new FriendsAddClick());
        btnEdit = (ToggleButton)findViewById(R.id.btn_friends_list_edit);
        btnEdit.setOnClickListener(new FriendsEditClick());
        btnRadar = (Button)findViewById(R.id.btn_friends_list_radar);
        btnRadar.setOnClickListener(new FriendListRadarClick());
        btnEnemies = (Button)findViewById(R.id.btn_friends_list_enemies);
        btnEnemies.setOnClickListener(new FriendListEnemiesClick());
    }

    public void InitData(){
        friendsCollection = new ArrayList<Contact>();
        for (int i = 0; i < 6; i++){
            Contact friend = new Contact();
            friend.setName("zjl");
            friend.setNumber("13128531850");
            friendsCollection.add(friend);
        }
    }

    class FriendsAddClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
            View dialogview = View.inflate(FriendsActivity.this, R.layout.dialog_add_friend, null);
            final AlertDialog dialog = builder.create();
            dialog.setView(dialogview);
            final EditText friendName = (EditText)dialogview.findViewById(R.id.txt_friend_name);
            final EditText friendNumber = (EditText)dialogview.findViewById(R.id.txt_friend_number);
            Button dialogOk = (Button)dialogview.findViewById(R.id.btn_dialog_ok);
            dialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = friendName.getText().toString();
                    String number = friendNumber.getText().toString();
//                    Contact friend = new Contact();
//                    friend.setName(name);
//                    friend.setNumber(number);
//                    friendsCollection.add(friend);
                    friendsListAdapter.addData(name, number);
                    friendsListAdapter.notifyDataSetChanged();
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

    class FriendsEditClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (friendsListAdapter.getIsBtnVisible()){
                friendsListAdapter.setIsBtnVisible(false);
                friendsListAdapter.notifyDataSetChanged();
            }
            else {
                friendsListAdapter.setIsBtnVisible(true);
                friendsListAdapter.notifyDataSetChanged();
            }
        }
    }

    class FriendListRadarClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(FriendsActivity.this, RadarActivity.class);
            startActivity(intent);
        }
    }

    class FriendListEnemiesClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(FriendsActivity.this, EnemiesActivity.class);
            startActivity(intent);
        }
    }
}
