package com.example.appayn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.adapter.Poi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PositionSurCarte extends Activity implements OnMapClickListener, OnMapLongClickListener,
    OnMarkerClickListener {
    private GoogleMap gMap;
    LatLng maposition, location;
    final LatLng CENTER = new LatLng(36.767148, 10.284319);
    String motCle;
    MapManager mm;
    List<Poi> listePOI;
    ProgressDialog myPd_ring;
    LocationManager locationManager;
    LatLng center;
     LatLng poi;
    @Override
    @SuppressLint("NewApi")
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_activity);
        
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
        
        // Zoom in the Google Map
        // gMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        
        // ecoute map
        gMap.setOnMapClickListener(this);
       
        // ecoute sur marker
        gMap.setOnMarkerClickListener(this);
      
        // ecoute long clique sur map
        gMap.setOnMapLongClickListener(this);
        mm = new MapManager();
    }
    
    @Override
    public void onMapLongClick(final LatLng point) {
    	
        //clear map
    	gMap.clear();
    	
    	
        // add marker
        gMap.addMarker(new MarkerOptions().position(point).title(point.toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
       
        
        // affecter la position -->globale
        maposition = point;
        
        // open dialog confirmation
        openDialogToConfirmPosition();
        
    }
    
    public void openDialogToConfirmPosition() {
        
        /* Alert Dialog Code Start */
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("confirmation de votre position"); // Set Alert dialog title
        // here
        alert.setMessage("Merci de confirmer votre position source!"); // Message
                                                                       // here
        
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                
                // get mots cles
                motCle = extractStringFromActivity(getIntent(), "POI");
                
                // progress bar : attend la reception des pois
                myPd_ring = ProgressDialog.show(PositionSurCarte.this, "Recherche des poi",
                                                "veuillez attendre la reception des poi..", true);
                myPd_ring.setCancelable(true);
                // myPd_ring.setCanceledOnTouchOutside(false);
                
                // recuperer la liste des POI
                listePOI = searchPOI(motCle, maposition);
                
                // Ajouter ma position dans la liste
                addMyPositionToList(maposition);
                Log.v("test","liste des pois+ mapoistion");
                
                // dessiner les points sur la carte
                
                mm.drawPOIOnMap(gMap, listePOI);
            }
        });
        
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                // Canceled.
                dialog.cancel();
                gMap.clear();
            }
        });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
    
    public void addMyPositionToList(final LatLng myPosition) {
    	Log.v("test"," lattttttt "+myPosition.latitude);
    	Log.v("test"," longggggg "+myPosition.longitude);
    	listePOI.add(LatLngToPoiTransformer.transform(myPosition));
        myPd_ring.dismiss();
    }
    
    public String extractStringFromActivity(final Intent intent, final String extratName) {
        final Bundle bundle = intent.getExtras();
        return bundle.getString(extratName);
    }
    
    public List<Poi> searchPOI(final String motCle, final LatLng maposition) {
    	// parse mots cle

		MyTools tool = new MyTools();
		String[] Keyswords = tool.parsekeywords(motCle);

		// envoyer la requete de recherche vers le serveur

		int rayonDeRecherche = 10;

		SearchTask asyncTask = new SearchTask(this, false);
		SearchQuery querySearch = new SearchQuery(Keyswords, maposition.latitude,maposition.longitude,
				rayonDeRecherche);
		asyncTask.execute(querySearch );
		boolean problem = false;
		  List<Poi> result = new ArrayList<Poi>();
		  try {
		   result.addAll(asyncTask.get(10, TimeUnit.SECONDS));
			
		} catch (TimeoutException tex) {

			Toast.makeText(PositionSurCarte.this, "Vous avez depasser le temps de recherche.", Toast.LENGTH_LONG).show();
			Log.v("test","Task has benn canceled"+tex.getMessage());
			problem = true;
			// loguer + message erreur
		} catch (InterruptedException iex) {
			Log.v("test","Interrupted Exception");
			iex.printStackTrace();
			final Toast toast = Toast.makeText(PositionSurCarte.this, "Problème interne", Toast.LENGTH_SHORT);
	        toast.show();
			problem = true;
			// loguer + message erreur
		} catch (ExecutionException eex) {

			Log.v("test","Execution Exception");
			 eex.printStackTrace();
			 final Toast toast = Toast.makeText(PositionSurCarte.this, "Problème interne", Toast.LENGTH_SHORT);
		        toast.show();
			 problem = true;
			// loguer + message erreur
		}
		// analyser la reponse et recuperer la liste de
		if (result.isEmpty()&& problem==false) {
			// il ya eu un pb message d'erreur.
			//Toast.makeText(PositionSurCarte.this, "pas des points d'interet dans cette region", Toast.LENGTH_LONG).show();
			Log.v("test", "resultat vide :"+result);
		}
	

		Log.v("test", "resultat searchpoi :"+result);
		

		return result;
    }
    
    @Override
    public void onMapClick(final LatLng point) {
        
        gMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        
    }
    
    @Override
    public boolean onMarkerClick(final Marker dest) {
    	MyTools tool=new MyTools();
    	if(tool.isSamePosition(dest, maposition))
    	{
    		

    		Toast.makeText(PositionSurCarte.this, "Ma position", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
        /* Alert Dialog Code Start*/
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("veuillez choisir votre destination"); // Set Alert dialog title here
        alert.setMessage("voulez vous confirmer?"); // Message here
        
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
            
                poi = dest.getPosition();
                
                gMap.clear();
                //redessiner les points
                mm.drawPOIOnMap(gMap, listePOI);
                
                iteniraire(poi);
                
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
    	}
        return false;
    }
    
    
    
    public void iteniraire(final LatLng poi) {
        new ItineraireTask(this, gMap, maposition, poi).execute();
    }
    
    
    
    
}
