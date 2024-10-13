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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import com.example.adapter.Poi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class SearchByAddress extends Activity implements OnMarkerClickListener, LocationListener {
    
    Context context;
    Geocoder geocoder = null;
    private GoogleMap gMap;
    LatLng maposition;
    MapManager mm;
    ProgressDialog myPd_ring;
    List<Poi> listePOI;
    LatLng center;
    EditText locale;
    String locationName;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;// 1000 * 0 * 1; // 1 minute
    Location location;
    LocationManager locationManager;
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.searchbyadress);
        //Edit text contient la ville
        locale = (EditText) findViewById(R.id.location);
        
        
        // map
        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        
        //test map
        if(gMap == null){
        	Log.v("test", "Map  null");	
        }
        
        
        // last position or tunisia
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Location lastpos = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastpos != null) {
            center = new LatLng(lastpos.getLatitude(), lastpos.getLongitude());
        } else {
            center = new LatLng(36.81808, 10.165733);
        }
        
        // centrer map sur la position "center"
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));
        
        
        //calcul iteniraire
        itenirairebyadress();
        
        gMap.setOnMarkerClickListener(this);
        mm = new MapManager();
    }
    
    
  //calcul iteniraire
    public void itenirairebyadress() {
    	geocoder = new Geocoder(this);
        final Button geoBtn = (Button) findViewById(R.id.geocodeBtn);
        geoBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
            	

                locationName = ((EditText) findViewById(R.id.location)).getText().toString();
                 if (locationName.compareTo("") == 0) {
                	
                	 locale.setError("champ obligatoire");
                	 
                	 
                 }else
                 {
                	 locale.setError(null);
                
                if (startGPS()) {
                    String motCle;
                    try {
                    	

                   	 Toast.makeText(getBaseContext(), "Recherche de votre adresse en cours...", Toast.LENGTH_SHORT).show();
                          
                          //map
                          gMap.clear();
                          
                          
                        // fermer clavier
                        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.location)).getWindowToken(), 0);
                        
                        
                         
                         
                         //envoyer la requete au serveur avec parametre=ville
                        
                         List<Address> addressList =null;
                         try
                         {
                        	 addressList= geocoder.getFromLocationName(locationName, 5);
                         }catch(Exception e){
                        	 Log.v("test","pas de reponse de google");
                         }
                        if (addressList != null && addressList.size() > 0) {
                        	
                        	
                            
                            
                            // position source
                            Log.v("test", "location getLatitude=  " + addressList.get(0).getLatitude());
                            Log.v("test", "location getLongitude=  " + addressList.get(0).getLongitude());
                            maposition = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(maposition));
                            
                            // Zoom in the Google Map
                            gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            
                            // get mots clÃ©s
                            motCle = extractStringFromActivity(getIntent(), "POI");
                            
                            // recuperer la liste des POI
                            listePOI = searchPOI(motCle, maposition);
                            
                            // Ajouter ma position dans la liste
                            addMyPositionToList(maposition);
                            
                            // dessiner les points sur la carte
                            mm.drawPOIOnMap(gMap, listePOI);
                            
                        
                            
                        } else {
                        	//locale.setError("Champ obligatoire");
                        	Toast.makeText(SearchByAddress.this, "Il y a eu unprobleme. veuillez ressayer", Toast.LENGTH_LONG).show();
                        	
                        }
                    } catch (final Exception e) {
                    	//Log.e("SearchByAdresse","exception")
                        e.printStackTrace();
                       

                		Toast.makeText(SearchByAddress.this, "Serveur injoignable ", Toast.LENGTH_LONG).show();
                      //  locale.setError("adresse incorrecte");
                    }
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
    
    public void addMyPositionToList(final LatLng myPosition) {
        listePOI.add(LatLngToPoiTransformer.transform(myPosition));
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

    			Toast.makeText(SearchByAddress.this, "Vous avez depasser le temps de recherche.", Toast.LENGTH_LONG).show();
    			Log.v("test","Task has benn canceled"+tex.getMessage());
    			problem = true;
     			// loguer + message erreur
     		} catch (InterruptedException iex) {
     			Log.v("test","Interrupted Exception");
    			iex.printStackTrace();
    			final Toast toast = Toast.makeText(SearchByAddress.this, "Problème interne", Toast.LENGTH_SHORT);
 		        toast.show();
    			problem = true;
     			// loguer + message erreur
     		} catch (ExecutionException eex) {

    			Log.v("test","Execution Exception");
    			 eex.printStackTrace();
    			 final Toast toast = Toast.makeText(SearchByAddress.this, "Problème interne", Toast.LENGTH_SHORT);
 		        toast.show();
    			 problem = true;
     			// loguer + message erreur
     		}
     		// analyser la reponse et recuperer la liste de
     		if (result.isEmpty()&& problem==false) {
     			// il ya eu un pb message d'erreur.
     			//Toast.makeText(SearchByAddress.this, "pas des points d'interet dans cette region", Toast.LENGTH_LONG).show();
     			Log.v("test", "resultat  vide:"+result);
     		}

    		Log.v("test", "resultat searchpoi :"+result);
     		return result;
    
    }
    
    public void iteniraire(final LatLng poi) {
        new ItineraireTask(this, gMap, maposition, poi).execute();
    }
    
    @Override
    public boolean onMarkerClick(final Marker dest) {
    	MyTools tool=new MyTools();
    	if(tool.isSamePosition(dest, maposition))
    	{
    		

    		Toast.makeText(SearchByAddress.this, " Ma position", Toast.LENGTH_LONG).show();
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
                
                final LatLng poi = dest.getPosition();
                gMap.clear();
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
    
    @Override
    public void onLocationChanged(final Location location) {
        // TODO Auto-generated method stub
        
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
