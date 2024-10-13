package com.example.appayn;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.util.Log;

public class MyTools {
	//attributs
	
	
	
		
		
		
		
	//constructeur
	public MyTools()
	{
		super();
	
	}
	
	
	
	
	
	
	
	
	//methodes
	
	//methode1: comparer 2 postions GPS
	public boolean isSamePosition(Marker destination,LatLng maposition)
	{
		// 4 chaines de caracteres pour recuperer les 5 chiffres apres la virgule
    	double mapositionlatitude=maposition.latitude;
		String mapositionlatitude1 =	String.format("%.5f", mapositionlatitude);
		
    	
		

    	double mapositionlongitude=maposition.longitude;
		String mapositionlongitude1 =	String.format("%.5f", mapositionlongitude);
	
		
		

    	double destlongitude=destination.getPosition().longitude;
		String destlongitude1 =	String.format("%.5f", destlongitude);
		
		
		

    	double destlatitude=destination.getPosition().latitude;
		String destlatitude1 =	String.format("%.5f", destlatitude);
	
		
    		
    	if(mapositionlatitude1.equals(destlatitude1) && mapositionlongitude1.equals(destlongitude1)){
    		return true;
    	}
    	else 
    	{
    		return false;
    	}
	}
	
	
	//methode2: parser les mots clés
	public String[] parsekeywords(String motcle){
		//  les espaces "\\s"
	String[] Keyswords = motcle.split("\\s");
	for(int i=0;i<Keyswords.length;i++){
		Log.v("test","splited   "+Keyswords[i]);
	}
	return Keyswords;
	}
	
	
	
}
