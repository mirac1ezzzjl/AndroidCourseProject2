package com.example.zx50.myradar.util;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.example.zx50.myradar.model.Contact;
import com.example.zx50.myradar.model.ContactMarker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MyApplication extends Application {

    private final String key = "0512";
    public String fileFriends = "FriendsList.dat";
    public String fileEnemies = "EnemiesList.dat";

    private ArrayList<Contact> friendsCollection = null;
    private ArrayList<Contact> enemiesCollection = null;
    public BaiduMap baiduMap = null;
    private Map<String, ContactMarker> friendMarkers = null;
    private Map<String, ContactMarker> enemyMarkers = null;

    private double latitude = 0;
    private double longitude = 0;



    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        friendsCollection = load(fileFriends);
        //if (friendsCollection == null) {
            friendsCollection = new ArrayList<>();
            Contact friend1 = new Contact();
            friend1.setName("me");
            friend1.setNumber("13128531850");
            friend1.setLatitude(22.261365);
            friend1.setLongitude(113.532989);
            friendsCollection.add(friend1);
        //}
        enemiesCollection = load(fileEnemies);
        if (enemiesCollection == null) {
            enemiesCollection = new ArrayList<>();
            Contact enemy1 = new Contact();
            enemy1.setName("xiaohong");
            enemy1.setNumber("4567123");
            enemy1.setLatitude(22.251953);
            enemy1.setLongitude(113.526421);
            enemiesCollection.add(enemy1);
        }
    }

    public BaiduMap getBaiduMap() {
        return baiduMap;
    }

    public void setBaiduMap(BaiduMap baiduMap) {
        this.baiduMap = baiduMap;
    }

    public String getKey() {
        return key;
    }

    public ArrayList<Contact> getFriendsCollection() {
        return friendsCollection;
    }

    public ArrayList<Contact> getEnemiesCollection() {
        return enemiesCollection;
    }

    public Contact getFriend(String number){
        for(Contact contact : friendsCollection){
            if (contact.getNumber().equals(number))
                return contact;
        }
        return null;
    }

    public Contact getEnemy(String number){
        for (Contact contact : enemiesCollection){
            if (contact.getNumber().equals(number))
                return contact;
        }
        return null;
    }

    public boolean addFriend(String name, String number){
        for (Contact i : friendsCollection){
            if (i.getNumber().equals(number))
                return false;
        }
        for (Contact i : enemiesCollection){
            if (i.getNumber().equals(number))
                return false;
        }
        Contact contact = new Contact();
        contact.setName(name);
        contact.setNumber(number);
        friendsCollection.add(contact);
        save(fileFriends, friendsCollection);
        return true;
    }

    public boolean addEnemy(String name, String number){
        for (Contact i : friendsCollection){
            if (i.getNumber().equals(number))
                return false;
        }
        for (Contact i : enemiesCollection){
            if (i.getNumber().equals(number))
                return false;
        }
        Contact contact = new Contact();
        contact.setName(name);
        contact.setNumber(number);
        enemiesCollection.add(contact);
        save(fileEnemies, enemiesCollection);
        return true;
    }

    public boolean delFriend(String number){
        for (Contact i : friendsCollection){
            if (i.getNumber().equals(number)){
                friendsCollection.remove(i);
                saveFriendsCollection();
                return true;
            }
        }
        return false;
    }

    public boolean delEnemy(String number){
        for (Contact i : enemiesCollection){
            if (i.getNumber().equals(number)){
                enemiesCollection.remove(i);
                saveEnemiesCollection();
                return true;
            }
        }
        return false;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<Contact> load(String filename){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(filename);
            ois = new ObjectInputStream(fis);
            return (ArrayList<Contact>) ois.readObject();
        } catch (FileNotFoundException e){
        } catch (Exception e) {
            e.printStackTrace();
            //反序列化失败 - 删除缓存文件
            if( e instanceof InvalidClassException) {
                File data = this.getFileStreamPath(filename);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch ( Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {

            }
        }
        return null;
    }

    public boolean save(String filename, Serializable ser) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFriendsCollection(){
        save(fileFriends, friendsCollection);
    }

    public void saveEnemiesCollection(){
        save(fileEnemies, enemiesCollection);
    }

    public Map<String, ContactMarker> getFriendMarkers() {
        return friendMarkers;
    }

    public void setFriendMarkers(Map<String, ContactMarker> friendMarkers) {
        this.friendMarkers = friendMarkers;
    }

    public Map<String, ContactMarker> getEnemyMarkers() {
        return enemyMarkers;
    }

    public void setEnemyMarkers(Map<String, ContactMarker> enemyMarkers) {
        this.enemyMarkers = enemyMarkers;
    }


}
