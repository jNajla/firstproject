package com.example.appayn;

import java.util.List;

import android.util.Log;

import com.example.adapter.Poi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapManager {
	public MapManager() {

	}

	public void drawPOIOnMap(final GoogleMap gMap, final List<Poi> listePOI) {

		int localCounter = 0;

		Double minLat = Double.MAX_VALUE;
		Double maxLat = Double.MIN_VALUE;
		Double minLon = Double.MAX_VALUE;
		Double maxLon = Double.MIN_VALUE;
		 MarkerOptions marker;

			
			for (final Poi item : listePOI) {
				localCounter++;
				final Double lat = item.getLocation()[0];
				final Double lon = item.getLocation()[1];

				maxLat = Math.max(lat, maxLat);
				minLat = Math.min(lat, minLat);
				maxLon = Math.max(lon, maxLon);
				minLon = Math.min(lon, minLon);

				marker = new MarkerOptions();
				marker.position(new LatLng(lat, lon));

				Log.v("test", " latitude :" + lat);
				Log.v("test", " longitude :" + lon);

				if (localCounter < listePOI.size()) {
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					marker.title("Point d'interet");
					
				} else {
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					marker.title("Ma position");
				
					
				
				}
				gMap.addMarker(marker);
				// gMap.addMarker(new
				// MarkerOptions().title("poi").position(item));

			}
			if (localCounter <= 1) {
					
				Log.v("test", "il y a un seul poi");
			} else {
				Log.v("test", "il y a des pois");
			final LatLng southWestLatLon = new LatLng(minLat, minLon);
			final LatLng northEastLatLon = new LatLng(maxLat, maxLon);
			centerMap(gMap, southWestLatLon, northEastLatLon);}
		}
	

	private void centerMap(final GoogleMap gMap, final LatLng sw,
			final LatLng nth) {

		// gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new
		// LatLngBounds(sw, nth), 50));
		// Log.v("test", "min latitude :" + sw.latitude);
		// Log.v("test", "min longitude :" + sw.longitude);
		// Log.v("test", "max latitude :" + nth.latitude);
		// Log.v("test", "max latitude :" + nth.longitude);

		// pour forcer le deplacement de la carte
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sw, 10));
		// deplacer la map =move
		gMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {

				// Move camera.
				gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
						new LatLngBounds(sw, nth), 10));
				// Remove listener to prevent position reset on camera move.
				gMap.setOnCameraChangeListener(null);
			}
		});

	}

}
