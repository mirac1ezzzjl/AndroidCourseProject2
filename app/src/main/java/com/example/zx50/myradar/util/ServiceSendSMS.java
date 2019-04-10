package com.example.zx50.myradar.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import java.util.ArrayList;

public class ServiceSendSMS extends Service {
    String phoneNumber = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    //我们这里执行服务启动都要做的操作
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        phoneNumber = intent.getStringExtra("number");
        MyApplication myApplication = (MyApplication)getApplicationContext();
        String smsContent = myApplication.getLatitude()+"/"+myApplication.getLongitude();
        String content = null;
        //AES加密部分
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

        System.out.println("onStartCommand invoke");
        return super.onStartCommand(intent, flags, startId);
    }

    //服务销毁时的回调
    @Override
    public void onDestroy() {
        System.out.println("onDestroy invoke");
        super.onDestroy();
    }
}
