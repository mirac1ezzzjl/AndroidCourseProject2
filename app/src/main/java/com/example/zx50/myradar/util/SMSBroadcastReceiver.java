package com.example.zx50.myradar.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.example.zx50.myradar.activity.RadarActivity;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    MyApplication application = null;
    private ContractMessage contractMessage;

    @Override
    public void onReceive(Context context, Intent intent) {
        //application = (MyApplication)context;
        Object [] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus != null){
            for (Object pdu : pdus) {
                //封装短信参数的对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = sms.getOriginatingAddress();//短信发送方
                String content = sms.getMessageBody();//发送内容
                //写自己的处理逻辑

                //这里补充AES解密
                try {
                    //realContent = AESencryptor.decrypt(content, application.getKey());
                    content = AESencryptor.decrypt(content, "123");
                }catch (Exception e){
                    e.printStackTrace();
                }

                System.out.println();

                //判断短信内容
                if (content.equals("where are you?")){
                    //发送自己所在位置
                    Intent intent1 = new Intent(context, ServiceSendSMS.class);
                    intent1.putExtra("number", sender);
                    intent1.setAction("com.example.zx50.myradar.util.ServiceSendSMS");
                    context.startService(intent1);
                    abortBroadcast();
                }
                // else if (content.matches("^(\\d*\\.)?\\d+/(\\d*\\.)?\\d+$")){
                else if (content.matches("^(\\d*\\.)?\\d+\\/(\\d*\\.)?\\d+$")){
                    //根据消息设置位置
//                Intent intent1 = new Intent(context, RefreshLocationService.class);
//                intent1.putExtra("number", sender);
//                intent1.putExtra("location", content);
//                intent1.setAction("com.example.zx50.myradar.util.RefreshLocationService");
//                context.startService(intent1);
                    //contractMessage.getContractMessage(sender, content);
                    Intent intent1 = new Intent(RadarActivity.ACTION_INTENT_RECEIVER);
                    intent1.putExtra("phoneNumber", sender);
                    intent1.putExtra("location", content);
                    context.sendBroadcast(intent1);
                    abortBroadcast();
                }
            }
        }

    }

    public interface ContractMessage {
        void getContractMessage(String number, String location);
    }

    public void setMessage(ContractMessage contractMessage){
        this.contractMessage = contractMessage;
    }
}
