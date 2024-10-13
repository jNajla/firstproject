package com.example.appayn;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

//recevoir la position par SMS
public class dialogpositionreceive extends Activity {
    Button annuler, localiser, itineraire;
    GoogleMap gMap;
    int localisation;
    Double longitude, latitude;
    LatLng point, maposition;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.dialogpositionreceive);
        final Bundle bundle = getIntent().getExtras();
        longitude = bundle.getDouble("longitude");
        Log.d("test", "longitude" + longitude);
        latitude = bundle.getDouble("latitude");
        Log.d("test", "latitude" + latitude);
        
        // setTheme(android.R.style.Theme_Dialog);
        final Dialog dialog = new Dialog(dialogpositionreceive.this);
        dialog.setContentView(R.layout.dialogpositionreceive);
        Log.v("test", "votre boite de dialogue");
        
        // Quitter la boite de dialogue
        annuler = (Button) dialog.findViewById(R.id.annuler);
        annuler.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
                
            }
        });
        // ajouter point d'interet prive
        localiser = (Button) dialog.findViewById(R.id.localiser);
        localiser.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                
                Log.v("test", "localiser");
                localisation = 1;
                final Intent intent = new Intent(dialogpositionreceive.this, ManageDataFromSMS.class);
                final Bundle bundle = new Bundle();
                bundle.putDouble("longitude", longitude);
                bundle.putDouble("latitude", latitude);
                bundle.putInt("localisation", localisation);
                intent.putExtras(bundle);
                startActivity(intent);
                
            }
            
        });
        
        // ajouter point d'int�r�t publique
        itineraire = (Button) dialog.findViewById(R.id.itineraire);
        itineraire.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                localisation = 2;
                final Intent intent = new Intent(dialogpositionreceive.this, ManageDataFromSMS.class);
                final Bundle bundle = new Bundle();
                bundle.putDouble("longitude", longitude);
                bundle.putDouble("latitude", latitude);
                bundle.putInt("localisation", localisation);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        dialog.show();
        dialog.setOnCancelListener(new OnCancelListener() {
            
            @Override
            public void onCancel(final DialogInterface dialog) {
                // TODO Auto-generated method stub
                
                finish();
            }
        });
    }
    
}
