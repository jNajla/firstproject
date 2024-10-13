package com.example.appayn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.adapter.ReceiveMessage;
import com.example.business.DbManager;

public class recevoirPosition extends BroadcastReceiver {
    int i = 1;
    Button annuler, localiser, itineraire;
    String[] str1 = new String[10];
    
    SharedPreferences sharedPreferences;
    
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v("test", "broadcast received");
        if (intent.getAction().equals("LAUNCHED")) {
            Log.v("test", " LAUNCHED ;) )))");
            // save the state of my application
            sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("etat", true);
            editor.commit();
            
        }
        if (intent.getAction().equals("CLOSED")) {
            Log.v("test", " CLOSED ;) ");
            sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("etat", false);
            editor.commit();
        }
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d("test", "sms received");
            final Bundle bundle = intent.getExtras();
            final String value = bundle.getString("value");
            Log.d("test", "application launched" + value);
            SmsMessage[] msgs = null;
            String str = "";
            if (bundle != null) {
                // ---retrieve the SMS message received---
                final Object[] pdus = (Object[]) bundle.get("pdus");
                Log.d("test", "bundle not null");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    str = msgs[i].getMessageBody();
                    Log.v("test", "messages:   " + msgs[i].getMessageBody());
                    if (str.startsWith("####")) {
                        str1 = str.split("\\*");
                        Log.v("test", "length " + str1.length);
                        int j;
                        for (j = 0; j < str1.length; j++) {
                            Log.v("test", "value " + j + " : " + str1[j]);
                            str1[j] = str1[j].replaceAll("\\s+", "");
                            
                        }
                        
                    }
                    // get my state
                    sharedPreferences = context.getSharedPreferences("myPrefs", 0);
                    final boolean etat_app = false;
                    final Boolean MonEtat = sharedPreferences.getBoolean("etat", etat_app);
                    if (MonEtat) {
                        Log.d("test", "yes");
                        intent = new Intent(context, dialogpositionreceive.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        final Bundle bundle2 = new Bundle();
                        final double latitude = Double.parseDouble(str1[2]);
                        Log.d("test", "latitude" + latitude);
                        bundle2.putDouble("latitude", latitude);
                        final double longitude = Double.parseDouble(str1[4]);
                        Log.d("test", "longitude" + longitude);
                        bundle2.putDouble("longitude", longitude);
                        intent.putExtras(bundle2);
                        context.startActivity(intent);
                        
                    } else {
                        Log.d("test", "No");
                        // if (mon etat == false) app closed
                        final DbManager dbb = new DbManager(context);
                        final double latitude = Double.parseDouble(str1[2]);
                        Log.d("test", "latitude" + latitude);
                        final double longitude = Double.parseDouble(str1[4]);
                        Log.d("test", "longitude" + longitude);
                        // final int phonenumber = Integer.parseInt(str1[3]);
                        // Log.d("test", "phonenumber" + phonenumber);
                        dbb.AddEntryReceiveMessage(new ReceiveMessage(latitude, longitude, /*phonenumber*/123456));
                    }
                    // ---display the new SMS message---
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
            }
        }
        
    }
    
    private void runOnUiThread(final Runnable runnable) {
        // TODO Auto-generated method stub
        
    }
}
