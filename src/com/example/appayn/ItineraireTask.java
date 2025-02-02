package com.example.appayn;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ItineraireTask extends AsyncTask<Void, Integer, Boolean> {
    /*******************************************************/
    /**
     * CONSTANTES. /
     *******************************************************/
    private static final String TOAST_MSG = "Calcul de l'itineraire en cours";
    private static final String TOAST_ERR_MAJ = "Impossible de trouver un itineraire,merci de verifier la connexion internet";
    
    /*******************************************************/
    /**
     * ATTRIBUTS. /
     *******************************************************/
    private final Context context;
    private final GoogleMap gMap;
    private final LatLng editDepart;
    private final LatLng editArrivee;
    private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();
    ProgressDialog myPd_ring;
    /*******************************************************/
    /**
     * METHODES / FONCTIONS. /
     *******************************************************/
    /**
     * Constructeur.
     * 
     * @param context
     * @param gMap
     * @param editDepart
     * @param editArrivee
     */
    public ItineraireTask(final Context context, final GoogleMap gMap, final LatLng editDepart, final LatLng editArrivee) {
        this.context = context;
        this.gMap = gMap;
        this.editDepart = editDepart;
        this.editArrivee = editArrivee;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPreExecute() {
        //Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
        myPd_ring = ProgressDialog.show(context, "Calcul de l'itineraire",
                "veuillez attendre le calcul de l'itineraire ... ", true);
        myPd_ring.setCancelable(true);
      
    }
    
    /***
     * {@inheritDoc}
     */
    
    private String getDirectionsUrl(final LatLng origin, final LatLng dest) {
        
        // Origin of route
        final String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        
        // Destination of route
        final String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        
        // Sensor enabled
        final String sensor = "sensor=false";
        
        // Building the parameters to the web service
        final String parameters = str_origin + "&" + str_dest + "&" + sensor;
        
        // Output format
        final String output = "xml";
        
        // Building the url to the web service
        final String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.v("test","url    :"+url);
        return url;
    }
    
    @Override
    protected Boolean doInBackground(final Void... params) {
    	
        try {
            final String url = getDirectionsUrl(editDepart, editArrivee);
            
            // Appel du web service
            final InputStream stream = new URL(url.toString()).openStream();
            
            // Traitement des données
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringComments(true);
            
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         
            final Document document = documentBuilder.parse(stream);
            document.getDocumentElement().normalize();
            
            // On recupere d'abord le status de la requete
            final String status = document.getElementsByTagName("status").item(0).getTextContent();
            if (!"OK".equals(status)) {
                return false;
            }
            
            // On recupere les steps
            final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
            final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
            final int length = nodeListStep.getLength();
            
            for (int i = 0; i < length; i++) {
                final Node nodeStep = nodeListStep.item(i);
                
                if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elementStep = (Element) nodeStep;
                    
                    // On décode les points du XML
                    decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                }
            }
            
            return true;
        } catch (final Exception e) {
            return false;
        }
        
    }
    
    /**
     * Méthode qui decode les points en latitudes et longitudes
     * 
     * @param points
     */
    private void decodePolylines(final String encodedPoints) {
        int index = 0;
        int lat = 0, lng = 0;
        
        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;
            
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            
            final int dlat = (result & 1) != 0 ? ~(result >> 1) : result >> 1;
            lat += dlat;
            shift = 0;
            result = 0;
            
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            
            final int dlng = (result & 1) != 0 ? ~(result >> 1) : result >> 1;
            lng += dlng;
            
            lstLatLng.add(new LatLng(lat / 1E5, lng / 1E5));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostExecute(final Boolean result) {

    	// fermer la boite de dialog pour eviter le crash
    	if(myPd_ring.isShowing()){
    		Log.v("test","dialog is showing");
    		myPd_ring.dismiss();
    		Log.v("test","myPd_ring"+myPd_ring);
    		}
       
        if (!result) {
            Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();
           // myPd_ring.dismiss();
        } else {
        	// myPd_ring.dismiss();
            // On déclare le polyline, c'est-à-dire le trait (ici bleu) que l'on ajoute sur la carte pour tracer l'itinéraire
            final PolylineOptions polylines = new PolylineOptions();
            polylines.color(Color.BLUE);
            
            // On construit le polyline
            for (final LatLng latLng : lstLatLng) {
                polylines.add(latLng);
            }
            
            // On déclare un marker vert que l'on placera sur le départ
            final MarkerOptions markerA = new MarkerOptions();
            markerA.position(lstLatLng.get(0));
            markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            
            // On déclare un marker rouge que l'on mettra sur l'arrivée
            final MarkerOptions markerB = new MarkerOptions();
            markerB.position(lstLatLng.get(lstLatLng.size() - 1));
            markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            
            // On met à jour la carte
            // gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLatLng.get(0), 10));
            // gMap.addMarker(markerA);
            gMap.addPolyline(polylines);
            
            // gMap.addMarker(markerB);
        }
    }
}
