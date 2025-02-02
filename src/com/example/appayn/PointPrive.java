package com.example.appayn;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adapter.PointInteretPrive;
import com.example.business.DbManager;

public class PointPrive extends Activity implements LocationListener {
    boolean start = false;
    Button btnquitter, btnposition;
    Location location;
    
    ProgressDialog myPd_ring;
    LocationManager locationManager = null;
    double latitude, longitude;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;// 1000 * 0 * 1; // 1 minute
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pointprive);
        
        // quitter
        btnquitter = (Button) findViewById(R.id.btnquitter);
        btnquitter.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                finish();
            }
        });
        // ajouter la position
        btnposition = (Button) findViewById(R.id.btnposition);
        btnposition.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                
                // hide keyboard
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.editnom)).getWindowToken(), 0);
                final EditText editnom = (EditText) findViewById(R.id.editnom);
                final String nom = ((EditText) findViewById(R.id.editnom)).getText().toString();
                if (nom.compareTo("") == 0) {
                    editnom.setError("champ obligatoire");
                    Log.v("test", "  :vous devez remplir les champs vide.   ");
                    
                    start = false;
                } else {
                    editnom.setError(null);
                    if (startGPS()) {
                        
                        start = true;
                        myPd_ring = ProgressDialog.show(PointPrive.this, "Enregistrer ma position",
                                                        "veuillez attendre la reception de votre position..", true);
                        myPd_ring.setCancelable(true);
                        // myPd_ring.setCanceledOnTouchOutside(false);
                    }
                    
                }
            }
        });
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
    public void onLocationChanged(final Location location) {
        DbManager dbb;
        // TODO Auto-generated method stub
        if (location != null && start == true) {
            
            myPd_ring.dismiss();
            final String nom = ((EditText) findViewById(R.id.editnom)).getText().toString();
            Log.v("test", "  :nomPointInteret:   " + nom);
            // r�cuperer la position GPS
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.v("test", "  :latitude:   " + latitude);
            Log.v("test", "  :longitude:   " + longitude);
            dbb = new DbManager(this);
            dbb.AddEntryPointPrive(new PointInteretPrive(nom, latitude, longitude));
            final Toast toast = Toast.makeText(PointPrive.this, "Votre position a �t� sauvegardee", Toast.LENGTH_SHORT);
            toast.show();
            dbb.closeMydb();
            finish();
        }
        
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        // fermer la boite de dialog pour eviter le crash
        if (myPd_ring != null) {
            myPd_ring.dismiss();
        }
        
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        
        Log.v("test", "arret d'aquisition GPS");
    }
    
    @Override
    public void onProviderDisabled(final String provider) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onProviderEnabled(final String provider) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        // TODO Auto-generated method stub
        
    }
    
}
