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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.adapter.Poi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AutourMoi extends Activity implements LocationListener,
		OnMarkerClickListener {
	private GoogleMap gMap;

	double latitude, longitude;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0;// 1000 * 0 * 1; // 1
														// minute
	Location location;
	LocationManager locationManager;
	boolean loc = false;
	ProgressDialog myPd_ring;
	Marker marker;
	LatLng maposition;
	MapManager mm;
	List<Poi> listePOI;
	LatLng center;
	String motCle;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map_activity);
		if (startGPS()) {
			// recevoir la position
			final Bundle bundle = getIntent().getExtras();
			loc = bundle.getBoolean("startSearching");
			// progress bar : attend la reception des pois
			myPd_ring = ProgressDialog.show(AutourMoi.this,
					"Recherche de votre position",
					"veuillez attendre la reception de votre position..", true);
			myPd_ring.setCancelable(true);
			// myPd_ring.setCanceledOnTouchOutside(false);

		}
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		Log.v("test", "motCle dans coCreate  :" + motCle);
		if (gMap == null) {
			Log.v("test", "map null");
		}
		// final LatLng tunis = new LatLng(36.81808, 10.165733);
		// gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tunis, 16));

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		final Location lastpos = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastpos != null) {
			center = new LatLng(lastpos.getLatitude(), lastpos.getLongitude());
		} else {
			center = new LatLng(36.81808, 10.165733);
		}
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));
		/*
		 * // recevoir la position final Bundle bundle =
		 * getIntent().getExtras(); loc = bundle.getBoolean("startSearching");
		 * // progress bar : attend la reception des pois myPd_ring =
		 * ProgressDialog.show(AutourMoi.this, "Recherche de votre position",
		 * "veuillez attendre la reception de votre position..", true);
		 * myPd_ring.setCancelable(true); //
		 * myPd_ring.setCanceledOnTouchOutside(false);
		 */
		gMap.setOnMarkerClickListener(this);

		mm = new MapManager();
	}

	// gps
	private boolean startGPS() {

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		final boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// si le gps est active
		if (enabled) {
			if (location == null) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				Log.v("test", " GPS updating ...");

			}
			final boolean enabled2 = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (enabled2) {
				if (location == null) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.v("test", " Network updating ...");
				}
			}
			return true;
		} else {
			/* Alert Dialog Code Start */
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Start GPS"); // Set Alert dialog title here
			alert.setMessage("Le GPS est desactive!! Voulez vous l'activer?"); // Message
																				// here

			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int whichButton) {

							startActivity(new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

						}
					});

			alert.setNegativeButton("CANCEL",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int whichButton) {

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

		// loc = true ==> user click on button "autour de moi"
		if (loc == true) {
			Log.v("test","loc  "+loc);
			// enlever le progress bar lance avec l activite
			myPd_ring.dismiss();

			// recuperer ma position
			maposition = new LatLng(location.getLatitude(),
					location.getLongitude());
            gMap.moveCamera(CameraUpdateFactory.newLatLng(maposition));
			// get mots cles from field
			motCle = extractStringFromActivity(getIntent(), "poiKeys");
			Log.v("test", "motCle dans onLocation  :" + motCle);

			// recuperer la liste des POI du serveur
			listePOI = searchPOI(motCle, maposition);

			// Ajouter ma position dans la liste
			addMyPositionToList(maposition);

			// dessiner les points sur la carte
			mm.drawPOIOnMap(gMap, listePOI);
			// new ItineraireTask(this, gMap, maposition,
			// editArrivee).execute();
			// reinitialiser le parametre
			loc = false;

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// fermer la boite de dialog pour eviter le crash
		if (myPd_ring != null) {
			myPd_ring.dismiss();
		}
	}

	public void addMyPositionToList(final LatLng myPosition) {
		listePOI.add(LatLngToPoiTransformer.transform(myPosition));
	}

	public String extractStringFromActivity(final Intent intent,
			final String extratName) {
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
		SearchQuery querySearch = new SearchQuery(Keyswords,
				maposition.latitude, maposition.longitude, rayonDeRecherche);
		asyncTask.execute(querySearch);
		boolean problem = false;
		List<Poi> result = new ArrayList<Poi>();
		long startTime=System.currentTimeMillis();
		try {
			
			//timeOut=1s doit etre < a celui dans asynctask
			result.addAll(asyncTask.get(10, TimeUnit.SECONDS));

		} catch (TimeoutException tex) {
			Log.v("test", "Task has benn canceled " + tex.getMessage()+" after "+(System.currentTimeMillis()-startTime));
			Toast.makeText(AutourMoi.this,
					"Vous avez depasser le temps de recherche.",
					Toast.LENGTH_LONG).show();
			
			problem = true;
			// loguer + message erreur
			
		} catch (InterruptedException iex) {
			Log.v("test", "Interrupted Exception");
			iex.printStackTrace();
			// loguer + message erreur
			 final Toast toast = Toast.makeText(AutourMoi.this, "Problème interne", Toast.LENGTH_SHORT);
		        toast.show();
			problem = true;
			
		} catch (ExecutionException eex) {
			
			// loguer + message erreur
			Log.v("test", "Execution Exception");
			eex.printStackTrace();
			 final Toast toast = Toast.makeText(AutourMoi.this, "Problème interne", Toast.LENGTH_SHORT);
		        toast.show();
			problem = true;
		}
		// analyser la reponse et recuperer la liste de poi
		if (result.isEmpty() && problem==false) {
			// il ya eu un pb message d'erreur.
			/*Toast.makeText(AutourMoi.this,
					"Pas des points d'interet dans cette region",
					Toast.LENGTH_LONG).show();*/
			Log.v("test", "resultat vide  :" + result);
		}

		Log.v("test", "resultat searchpoi :" + result);
		return result;
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
	public void onStatusChanged(final String provider, final int status,
			final Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// fermer la boite de dialog pour eviter le crash
		if (myPd_ring != null) {
			myPd_ring.dismiss();
		}
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	public void iteniraire(final LatLng poi) {

		new ItineraireTask(this, gMap, maposition, poi).execute();

	}

	@Override
	public boolean onMarkerClick(final Marker dest) {

		MyTools tool = new MyTools();
		if (tool.isSamePosition(dest, maposition)) {

			Toast.makeText(AutourMoi.this, " Ma position", Toast.LENGTH_LONG)
					.show();
		} else {

			/* Alert Dialog Code Start */
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("veuillez choisir votre destination "); // Set Alert
																	// dialog
																	// title
																	// here
			alert.setMessage("voulez vous confirmer?"); // Message here

			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int whichButton) {

							final LatLng poi = dest.getPosition();
							gMap.clear();
							mm.drawPOIOnMap(gMap, listePOI);
							iteniraire(poi);

						}
					});

			alert.setNegativeButton("CANCEL",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int whichButton) {

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
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		savedInstanceState.putString("motCle", motCle);
		Log.v("test", "key words after turn phone     motCle" + motCle);
		super.onSaveInstanceState(savedInstanceState);
	}

}
