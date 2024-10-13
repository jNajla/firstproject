package com.example.appayn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.adapter.SendReceivePosition;
import com.example.business.DbManager;

public class EnvoyerPosition extends Activity implements LocationListener {
    Location location;
    AutoCompleteTextView numberphone;
    Button sendpositionnetwork, finish, sendpositionsms;
    double latitude;
    double longitude;
    String message;
    String phoneNumber;
    boolean sendnetwork, sendsms;
    PendingIntent sentIntent;
    ProgressDialog myPd_ring;
    PendingIntent deliveryIntent;
    LocationManager locationManager;
    IntentFilter filter;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;// 1000 * 0 * 1; // 1 minute
    String MyPhoneNumber;
    DbManager db;
    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.envoyerposition);
        mPeopleList = new ArrayList<Map<String, String>>();
        Log.v("test", "On create envoie position");
        PopulatePeopleList();
        // GPS
        
        /* My phone number
          MyPhoneNumber = getMyPhoneNO();
          Log.v("test", MyPhoneNumber);*/
        numberphone = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview, new String[] {
            "Name", "Phone", "Type"
        }, new int[] {
            R.id.ccontName, R.id.ccontNo, R.id.ccontType
        });
        numberphone.setAdapter(mAdapter);
        numberphone.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3) {
                // TODO Auto-generated method stub
                @SuppressWarnings("unchecked")
                final Map<String, String> map = (Map<String, String>) arg0.getItemAtPosition(arg2);
                
                map.get("Name");
                final String number = map.get("Phone");
                numberphone.setText(number);
            }
            
        });
        
        finish = (Button) findViewById(R.id.finish);
        // txtMessage = (EditText) findViewById(R.id.txtMessage);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                finish();
            }
        });
        
        sendpositionnetwork = (Button) findViewById(R.id.envoinetwork);
        sendpositionnetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // hide keyboard
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.autoCompleteTextView1)).getWindowToken(), 0);
                
                Toast.makeText(getBaseContext(), "En cours de developpement.", Toast.LENGTH_SHORT).show();
                
                /* phoneNumber = numberphone.getText().toString();
                 if (phoneNumber.compareTo("") == 0) {
                     numberphone.setError("Champ obligatoire");
                     Log.v("test", "  :S'il vous plait entrez le numero de votre detinataire.   ");
                     Toast.makeText(getBaseContext(), "S'il vous plait entrez le numero de votre detinataire.",
                                    Toast.LENGTH_SHORT).show();
                     sendnetwork = false;
                 } else {
                     numberphone.setError(null);
                     sendsms = false;
                     if (startGPS()) {
                         sendnetwork = true;
                         myPd_ring = ProgressDialog.show(EnvoyerPosition.this, "Envoyer ma position",
                                                         "veuillez attendre la reception de votre position..", true);
                         
                         myPd_ring.setCancelable(true);
                     }
                 }*/
            }
            
        });
        sendpositionsms = (Button) findViewById(R.id.envoisms);
        sendpositionsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // hide keyboard
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.autoCompleteTextView1)).getWindowToken(), 0);
                
                phoneNumber = numberphone.getText().toString();
                phoneNumber = phoneNumber.replace(" ", "");
                if (phoneNumber.compareTo("") == 0) {
                    numberphone.setError("Champ obligatoire");
                   /* Toast.makeText(getBaseContext(), "S'il vous plait entrez le numero de votre detinataire.",
                                   Toast.LENGTH_SHORT).show();*/
                    sendsms = false;
                } else if (!isNumeric(phoneNumber)) {
                    numberphone.setError("Votre numero est invalide");
                   /* Toast.makeText(getBaseContext(), "S'il vous plait entrez un numero valide", Toast.LENGTH_SHORT)
                        .show();*/
                    sendsms = false;
                    
                } else if (!isSimExists()) {
                    numberphone.setError("Inserer carte SIM");
                    Toast.makeText(getBaseContext(), "Envoi impossible,merci d'inserer une carte SIM",
                                   Toast.LENGTH_SHORT).show();
                    sendsms = false;
                    
                } else {
                    numberphone.setError(null);
                    sendsms = false;
                    if (startGPS()) {
                        sendsms = true;
                        
                        myPd_ring = ProgressDialog.show(EnvoyerPosition.this, "Envoyer ma position",
                                                        "veuillez attendre la reception de votre position..", true);
                        myPd_ring.setCancelable(true);
                    }
                }
                
            }
            
        });
        
        sendBroadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(final Context arg0, final Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Log.v("test", "Message envoye");
                        Toast.makeText(getBaseContext(), "Message envoye", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.v("test", "Generic failure");
                        Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.v("test", "No service");
                        Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.v("test", "Null PDU");
                        Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.v("test", "Radio off");
                        Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        
        filter = new IntentFilter(SENT);
        registerReceiver(sendBroadcastReceiver, filter);
    }
    
    public boolean isNumeric(final String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
    
    public static final SmsMessage[] getMessagesFromIntent(final Intent intent) {
        Log.v("test", "getMessagesFromIntent");
        final Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        if (messages == null || messages.length == 0) {
            Log.v("test", "return null");
            return null;
        }
        
        final byte[][] pduObjs = new byte[messages.length][];
        Log.v("test", "messages.length :" + messages.length);
        for (int i = 0, len = messages.length; i < len; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        
        final byte[][] pdus = new byte[pduObjs.length][];
        final SmsMessage[] msgs = new SmsMessage[pdus.length];
        Log.v("test", "pdus.length:" + pdus.length);
        for (int i = 0, count = pdus.length; i < count; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
            Log.v("test", "sms sms sms" + msgs[i].getOriginatingAddress());
        }
        
        return msgs;
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        registerReceiver(sendBroadcastReceiver, filter);
        
        super.onResume();
        
        // bien etudier le mecanisme de on resume / onpause .... TODO
        /*    sendBroadcastReceiver = new BroadcastReceiver() {
                
                @Override
                public void onReceive(final Context arg0, final Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Log.v("test", "Message envoye");
                            Toast.makeText(getBaseContext(), "Message envoye", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Log.v("test", "Generic failure");
                            Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Log.v("test", "No service");
                            Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Log.v("test", "Null PDU");
                            Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Log.v("test", "Radio off");
                            Toast.makeText(getBaseContext(), "Echec d'envoi", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };
            
            registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));*/
    }
    
    @SuppressWarnings("deprecation")
    public void PopulatePeopleList() {
        mPeopleList.clear();
        final Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (people.moveToNext()) {
            final String contactName = people.getString(people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            final String contactId = people.getString(people.getColumnIndex(BaseColumns._ID));
            final String hasPhone = people.getString(people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            
            if (Integer.parseInt(hasPhone) > 0) {
                // You know have the number so now query it like this
                final Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                                 null,
                                                                 ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                                     + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    // store numbers and display a dialog letting the user
                    // select which.
                    final String phoneNumber = phones.getString(phones
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    final String numberType = phones.getString(phones
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    final Map<String, String> NamePhoneType = new HashMap<String, String>();
                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);
                    if (numberType.equals("0")) {
                        NamePhoneType.put("Type", "Work");
                    } else if (numberType.equals("1")) {
                        NamePhoneType.put("Type", "Home");
                    } else if (numberType.equals("2")) {
                        NamePhoneType.put("Type", "Mobile");
                    } else {
                        NamePhoneType.put("Type", "Other");
                    }
                    // Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        // stopManagingCursor(people);
        people.close();
        // startManagingCursor(people);
    }
    
    // check internet
    public boolean CheckInternet() {
        final ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if (wifi.isConnected() || mobile.isConnected()) {
            return true;
        }
        return false;
    }
    
    // get my phone number :
    private String getMyPhoneNO() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        
        final String yourNumber = mTelephonyMgr.getLine1Number();
        return yourNumber;
    }
    
    public boolean isSimExists() {
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final int SIM_STATE = telephonyManager.getSimState();
        
        if (SIM_STATE == TelephonyManager.SIM_STATE_READY) {
            return true;
        } else {
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT: // SimState = "No Sim Found!";
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: // SimState = "Network Locked!";
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED: // SimState = "PIN Required to access SIM!";
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED: // SimState = "PUK Required to access SIM!"; // Personal Unblocking Code
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN: // SimState = "Unknown SIM State!";
                    break;
            }
            return false;
        }
    }
    
    // ---sends an SMS message to another device---
    private void sendSMS(final String phoneNumber, final String message) {
        try {
            
            final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            
            final SmsManager smsMgr = SmsManager.getDefault();
            smsMgr.sendTextMessage(phoneNumber, null, message, sentPI, null);
            
        } catch (final Exception e) {
            Toast.makeText(this, e.getMessage() + "!\n" + "Failed to send SMS", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        
    }
    
    class SMSReceiver extends BroadcastReceiver {
        
        private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
        
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent != null && intent.getAction() != null && ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
                
                Toast.makeText(getBaseContext(), "Sms Received", Toast.LENGTH_LONG).show();
                // Sms Received Your code here
            }
        }
    }
    
    // gps
    private boolean startGPS() {
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // si le gps est active
        if (enabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                                       MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.v("test", " GPS updating ...");
                
            }
            final boolean enabled2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (enabled2) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                                                           MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.v("test", " Network updating ...");
                }
            }
            return true;
        } else {
            /* Alert Dialog Code Start*/
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Start GPS"); // Set Alert dialog title here
            alert.setMessage("Le GPS est desactive!! Voulez vous l'activer?"); // Message here
            
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int whichButton) {
                    
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    
                }
            });
            
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int whichButton) {
                    
                    // Canceled.
                    dialog.cancel();
                    
                }
            });
            final AlertDialog alertDialog = alert.create();
            alertDialog.show();
            
            return false;
        }
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        /*  if (sendBroadcastReceiver != null) {
              unregisterReceiver(sendBroadcastReceiver);
          }*/
        // unregisterReceiver(deliveryBroadcastReceiver);
        super.onStop();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        
        /* if (sendBroadcastReceiver != null) {
             unregisterReceiver(sendBroadcastReceiver);
         }*/
        super.onPause();
    }
    
    @Override
    public void onLocationChanged(final Location location) {
        // TODO Auto-generated method stub
        // Log.v("test", "  :location received:   ");
        
        phoneNumber = numberphone.getText().toString();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        // location.getProvider();
        message = "####* " + "latitude:* " + latitude + " *longitude:* " + longitude;
        // Log.v("test", "  :message:   " + message);
        if (sendnetwork) {
            // CheckInternet();
            
            
            
            // sharePositionThroughInternet(); TODO
            Log.v("test", "  :envoi par internet:   ");
            db = new DbManager(this);
            db.AddEntrySendReceivePosition(new SendReceivePosition(longitude, latitude, MyPhoneNumber, phoneNumber));
            Toast.makeText(getBaseContext(), "position envoyé.", Toast.LENGTH_SHORT).show();
            Log.v("test", "finish");
            
            if (sendBroadcastReceiver != null) {
                unregisterReceiver(sendBroadcastReceiver);
            }
            myPd_ring.dismiss();
            finish();
            
        }
        if (sendsms) {
            
            
            Log.v("test", "value of send  :  " + sendsms);
            Log.v("test", "  :envoi par sms:   ");
            sendSMS(phoneNumber, message);
            sendsms = false;
            myPd_ring.dismiss();
            finish();
            
        }
        
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        /* if (sendBroadcastReceiver != null) {
             unregisterReceiver(sendBroadcastReceiver);
         }*/
        
        // fermer la boite de dialog pour eviter le crash
        if (myPd_ring != null) {
            myPd_ring.dismiss();
        }
        
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        super.onDestroy();
        
    }
    
    @Override
    public void onProviderDisabled(final String arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onProviderEnabled(final String arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onStatusChanged(final String arg0, final int arg1, final Bundle arg2) {
        // TODO Auto-generated method stub
        
    }
}
