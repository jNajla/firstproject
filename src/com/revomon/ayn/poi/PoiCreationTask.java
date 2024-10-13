package com.revomon.ayn.poi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.adapter.Poi;

public class PoiCreationTask extends AsyncTask<Poi, Integer, Poi> {
 
 /*******************************************************/
 /**
  * ATTRIBUTS. /
  *******************************************************/
 private final Context context;

 /*******************************************************/
 /**
  * METHODES / FONCTIONS. /
  *******************************************************/
 /**
  * Constructeur.
  * 
  */
 public PoiCreationTask(final Context context) {
  this.context = context;
 }

 /**
  * {@inheritDoc}
  */
 @Override
 protected void onPreExecute() {
  Log.v("test", "Recherche en cours    :");
  Toast.makeText(context, "Creation du point d'interet en cours", Toast.LENGTH_LONG).show();

 }

 @Override
 protected Poi doInBackground(final Poi... pois) {
  Poi poi = pois[0];

  try {
   PoiService.getInstance().create(poi);
  } catch (final Exception e) {
   e.printStackTrace();
  } 
  return poi;

 }

 
 /**
  * {@inheritDoc}
  */
 @Override
 protected void onPostExecute(final Poi result) {
  if (result.getId()!=null) {
   Toast.makeText(context, "Le point d'interet est creer avec succes", Toast.LENGTH_SHORT).show();
   Log.v("test", "Le point d'interet est creer avec succes :");
   
  
  } else {
   Toast.makeText(context, "Erreur lors de la creation de votre point d'interet", Toast.LENGTH_SHORT).show();
   Log.v("test", "Erreur lors de la creation de point d'interet   :");
  }
 }

}