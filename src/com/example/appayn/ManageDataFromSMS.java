package com.example.appayn;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ManageDataFromSMS extends Activity implements LocationListener {
    GoogleMap gMap;
    Double longitude, latitude;
    int localisation;
    LatLng point, maposition;
    boolean iteniraire = false;
    ProgressDialog myPd_ring;
    LocationManager locationManager;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 0 * 1; // 1 seconde
    Location location;
    LatLng center;
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_activity);
        startGPS();
        // map
        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Location lastpos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastpos != null) {
            center = new LatLng(lastpos.getLatitude(), lastpos.getLongitude());
        } else {
            Log.v("test", "toto");
            center = new LatLng(36.81808, 10.165733);
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));
        // recevoir la position
        final Bundle bundle = getIntent().getExtras();
        longitude = bundle.getDouble("longitude");
        latitude = bundle.getDouble("latitude");
        Log.v("test", " 2 lat: " + latitude + " -  long: " + longitude);
        
        localisation = bundle.getInt("localisation");
        
        // new LatLng(latitude, longitude);
        // si le bouton choisit est localiser
        if (localisation == 1) {
            Log.v("test", " 3 lat: " + latitude + " -  long: " + longitude);
            final LatLng point = new LatLng(latitude, longitude);
            gMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
            gMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            
            // Zoom in the Google Map
            gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            // si le bouton choisit est itineraire
        } else if (localisation == 2) {
            iteniraire = true;
            myPd_ring = ProgressDialog.show(ManageDataFromSMS.this, "itinéraire en cours de construction ",
                                            "veuillez attendre la reception de votre position..", true);
            myPd_ring.setCancelable(true);
            // myPd_ring.setCanceledOnTouchOutside(false);
        }
        /*     gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                 boolean doNotMoveCameraToCenterMarker = true;
                 
                 @Override
                 public boolean onMarkerClick(final Marker arg0) {
                     // TODO Auto-generated method stub
                     return doNotMoveCameraToCenterMarker;
                 }
             });*/
    }
    
    // gps
    
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
        List<LatLng> listePOI;
        // TODO Auto-generated method stub
        if (iteniraire == true) {
            // fermer la boite de dialogue
            myPd_ring.dismiss();
            // ma position + position reçu
            final LatLng point = new LatLng(latitude, longitude);
            maposition = new LatLng(location.getLatitude(), location.getLongitude());
            
            // recuperer la liste des POI
            listePOI = addPOIToList(point, maposition);
            
            // dessiner les points sur la carte
            final MapManager mm = new MapManager();
            mm.drawPOIOnMap(gMap, LatLngToPoiTransformer.tronsform(listePOI));
            new ItineraireTask(this, gMap, maposition, point).execute();
            iteniraire = false;
        }
        
    }
    
    public List<LatLng> addPOIToList(final LatLng point, final LatLng maposition) {
        
        final List<LatLng> listePOI = new ArrayList<LatLng>();
        
        listePOI.add(point);
        listePOI.add(maposition);
        
        return listePOI;
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
