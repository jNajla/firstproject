package com.example.appayn;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.example.adapter.Poi;
import com.google.android.gms.maps.model.LatLng;
//adaptateur
public class LatLngToPoiTransformer {

	public static List<Poi> tronsform(List<LatLng> listePOI) {
		//créer une liste de pois 
		Log.v("test"," LatLngToPoiTransformer");
		List<Poi> pois = new ArrayList<Poi>();
		for (LatLng latLng : listePOI) {
			pois.add(transform(latLng));
		}
		Log.v("test"," add this POI to POIS");
		return pois;
	}
// créer un objet poi qui va contenir que la position GPS
	public static Poi transform(LatLng latLng) {
		Poi poi = new Poi();

		Log.v("test"," transform");
		Log.v("test"," lattttttt"+latLng.latitude);
    	Log.v("test"," longggggg"+latLng.longitude);
		poi.setLocation(new double[]{latLng.latitude,latLng.longitude});
		Log.v("test"," add this position to POI");
		return poi;
	}

}
