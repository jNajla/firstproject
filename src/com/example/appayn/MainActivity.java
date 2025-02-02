package com.example.appayn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.business.DbManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity implements LocationListener {
    private GoogleMap gMap;
    
    // boite de diqlogueM traiter les donnees recu par SMS et stocker dans la base
    Button annuler, localiser, itineraire;
    
    // menu haut
    Button recevoir_position, ajouter_position, envoyer_position;
    
    // favoris
    Button restaurant, cafe, mecanicien, piz, pharmacie, supermarche, bouttonG, bouttonH, bouttonI;
    
    // methodes de recherche
    Button autourmoi, ville, carte;
    
    // boite de dialogue ajout position
    Button pointprive, pointpublique, btnquitter;
    
    Intent intent, i;
    boolean startiteniraire = false;
    
    String searchKeys, favorisName;
    
    EditText rechercher;
    LatLng point, maposition;
    double Latitude, Longitude;
    boolean loc = false;
    
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 0 * 1; // 1 minute
    Location location = null;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        // send broadcast
        mySendProadcast("LAUNCHED");
        
        // Open Database and crates table if necessary
        DbManager dbb = null;
        dbb = new DbManager(this);
        // check for SMS in database
        if (!dbb.isLocationFromSmsTableEmpty()) {
            // get SMS from database
            final LatLng smsLocation = dbb.getLocationFromSmsTable();
            Log.v("test", "smsLocation lat: " + smsLocation.latitude + " -  long: " + smsLocation.longitude);
            // clean database
            // dbb.deleteSmsTable();
            // open dialog for user : location or itinerary
            showLocateSmsDialog(smsLocation);
            
        }
        dbb.closeMydb();
        
        // TODO peut etre on n utilisera pas ce bouton
        receivePosition();
        
        // manage button: Add private or public POI
        addPosition();
        
        // manage button: Send My position
        sendPosition();
        
        // favoris list
        restau();
        coffee();
        mechanic();
        pizza();
        pharmacy();
        supermarket();
        hotel();
        hopital();
        taxi();
        
        // search methods
        chercher_autour_de_moi();
        chercher_a_partir_dune_position_sur_carte();
        chercher_a_partir_dune_ville();
        
    }
    
    // send to activity searchByAddress to choice the town ( src address)
    public void chercher_a_partir_dune_ville() {
        ville = (Button) findViewById(R.id.idL);
        ville.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                rechercher = (EditText) findViewById(R.id.editsearch);
                final String srch = ((EditText) findViewById(R.id.editsearch)).getText().toString();
                if (srch.compareTo("") == 0) {
                    rechercher.setError("champ obligatoire");
                } else {
                    rechercher.setError(null);
                    final Intent intent = new Intent(MainActivity.this, SearchByAddress.class);
                    final Bundle bundle = new Bundle();
                    bundle.putString("POI", searchKeys);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    
                }
            }
        });
        
    }
    
    // send to activity PositionSurCarte to choice a position on MAP( src address)
    public void chercher_a_partir_dune_position_sur_carte() {
        carte = (Button) findViewById(R.id.idM);
        carte.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                
                rechercher = (EditText) findViewById(R.id.editsearch);
                final String srch = ((EditText) findViewById(R.id.editsearch)).getText().toString();
                if (srch.compareTo("") == 0) {
                    
                    rechercher.setError("champ obligatoire");
                } else {
                    rechercher.setError(null);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("itineraire a� partir d'une position sur la carte"); // Set Alert dialog title
                    // here
                    alert.setMessage("Pour choisir une position,vous devez faire un long clic sur la carte."); // Message
                    // here
                    
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int whichButton) {
                            
                            final Intent intent = new Intent(MainActivity.this, PositionSurCarte.class);
                            final Bundle bundle = new Bundle();
                            bundle.putString("POI", searchKeys);
                            
                            intent.putExtras(bundle);
                            startActivity(intent);
                            
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
            }
        });
    }
    
    public void chercher_autour_de_moi() {
        autourmoi = (Button) findViewById(R.id.idK);
        autourmoi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText rechercher = (EditText) findViewById(R.id.editsearch);
                final String srch = ((EditText) findViewById(R.id.editsearch)).getText().toString();
                if (srch.compareTo("") == 0) {
                    rechercher.setError("champ obligatoire");
                    
                } else {
                    rechercher.setError(null);
                    searchingAround(searchKeys);
                    Log.v("test","searchingAround keyswords"+searchKeys);
                }
                
            }
        });
    }
    
    // This function is called when user click on search field. Defined in layout "activity main"
    public void search(final View view) {
        
        final Intent writeSearchKeys = new Intent(this, Afficher.class);
        startActivityForResult(writeSearchKeys, 10);
        
    }
    
    // process the return value from activity "afficher" ( search menu)
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        
        EditText searchField;
        
        super.onActivityResult(requestCode, resultCode, intent);
        
        // recuperer les mots cles et remplir le champs "recherche"
        if (resultCode == RESULT_OK && requestCode == 10) {
            searchKeys = intent.getStringExtra("returnedData");
            intent.putExtra("returnedData", searchKeys);
            setResult(RESULT_OK, intent);
            searchField = (EditText) findViewById(R.id.editsearch);
            searchField.setText(searchKeys);
            
        }
    }
    
    public void receivePosition() {
        recevoir_position = (Button) findViewById(R.id.id1);
        recevoir_position.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Toast.makeText(getBaseContext(), "En cours de developpement.", Toast.LENGTH_SHORT).show();
                
                /* Log.v("test", "recevoir une position");
                 final LatLng location = new LatLng(36.845285, 10.204529);
                 showLocateSmsDialog(location);*/
            }
        });
    }
    
    public void addPosition() {
        ajouter_position = (Button) findViewById(R.id.id2);
        ajouter_position.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                
                addPoiDialog();
                
            }
        });
    }
    
    /*
    public void callActiviteReceive() {
        final Intent i = new Intent(this, BootReceiver.class);
        startActivity(i);
    }*/
    
    public void callactiviteprive() {
        final Intent i = new Intent(this, PointPrive.class);
        startActivity(i);
    }
    
    public void callactivitepublique() {
        
        final Intent i = new Intent(this, PointPublique.class);
        startActivity(i);
        
    }
    
    public void sendPosition() {
        envoyer_position = (Button) findViewById(R.id.id3);
        envoyer_position.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                callActivitySendPosition();
                
            }
        });
    }
    
    private void callActivitySendPosition() {
        
        final Intent i = new Intent(this, EnvoyerPosition.class);
        startActivity(i);
        
    }
    
    public void restau() {
        restaurant = (Button) findViewById(R.id.idA);
        restaurant.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("restaurant");
            }
        });
    }
    
    private void searchingAround(final String searchingKeys) {
        final Intent intent = new Intent(MainActivity.this, AutourMoi.class);
        final Bundle bundle = new Bundle();
        bundle.putString("poiKeys", searchingKeys);
        bundle.putBoolean("startSearching", true);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    
    public void coffee() {
        cafe = (Button) findViewById(R.id.idB);
        cafe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("cafe");
            }
        });
    }
    
    public void mechanic() {
        mecanicien = (Button) findViewById(R.id.idC);
        mecanicien.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("mecanicien");
            }
        });
    }
    
    public void pizza() {
        piz = (Button) findViewById(R.id.idD);
        piz.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("pizza");
            }
        });
    }
    
    public void pharmacy() {
        pharmacie = (Button) findViewById(R.id.idE);
        pharmacie.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("pharmacie");
            }
        });
    }
    
    public void supermarket() {
        supermarche = (Button) findViewById(R.id.idF);
        supermarche.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("super marche");
            }
        });
    }
    
    public void hotel() {
        bouttonG = (Button) findViewById(R.id.idG);
        bouttonG.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("hotel");
            }
        });
    }
    
    public void hopital() {
        bouttonH = (Button) findViewById(R.id.idH);
        bouttonH.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("hopital");
            }
        });
    }
    
    public void taxi() {
        bouttonI = (Button) findViewById(R.id.idI);
        bouttonI.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchingAround("taxi");
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        final Intent in = new Intent("CLOSED");
        sendBroadcast(in);
        // dbb.closeMydb();
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /*  @SuppressLint("NewApi")
      @Override
      public void onLocationChanged(final Location location) {
          
          if (startiteniraire == true) {
              
              gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
              point = new LatLng(Latitude, Longitude);
              maposition = new LatLng(location.getLatitude(), location.getLongitude());
              gMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
          }
          
          new ItineraireTask(this, gMap, maposition, point).execute();
      }*/
    
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
    
    private void mySendProadcast(final String text) {
        final Intent in = new Intent(text);
        sendBroadcast(in);
    }
    
    /****************************************** Manage POI button functions **************************************/
    private void addPoiDialog() {
        Log.v("test", "ajouter un point d'interet :");
        // Cr�ation de la boite de dialogue
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent);
        
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog);
        
        // Quitter la boite de dialogue
        final Button quitter = (Button) dialog.findViewById(R.id.buttonquitter);
        quitter.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
            }
        });
        // ajouter point d'interet prive
        pointprive = (Button) dialog.findViewById(R.id.buttonprive);
        pointprive.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
                callactiviteprive();
                
            }
            
        });
        
        // ajouter point d'int�r�t publique
        pointpublique = (Button) dialog.findViewById(R.id.btnpublique);
        pointpublique.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
                
               // addPOIS();
                
                
               callactivitepublique();
            }
        });
        dialog.show();
        
    }
    
    /******************************* Manage SMS ******************/
    
    private void showLocateSmsDialog(final LatLng loc) {
        // get location
        final LatLng smsloc = loc;
        // open dialog
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent);
        
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialogpositionreceive);
        // dialog.setTitle("placer votre position");
        
        localiser = (Button) dialog.findViewById(R.id.localiser);
        localiser.setOnClickListener(new OnClickListener() {
            
            @SuppressLint("NewApi")
            @Override
            public void onClick(final View v) {
                // final int localisation = 1;
                Log.v("test", "smsloc lat: " + smsloc.latitude + " -  long: " + smsloc.longitude);
                
                processSmsInfo(1, smsloc);
                
            }
        });
        
        itineraire = (Button) dialog.findViewById(R.id.itineraire);
        itineraire.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                // final int localisation = 2;
                processSmsInfo(2, smsloc);
            }
        });
        // Quitter la boite de dialogue
        annuler = (Button) dialog.findViewById(R.id.annuler);
        annuler.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        dialog.show();
    }
    
    // send parameters to activity that process SMS data
    public void processSmsInfo(final int localisation, final LatLng loc) {
        final Intent intent = new Intent(MainActivity.this, ManageDataFromSMS.class);
        final Bundle bundle = new Bundle();
        bundle.putDouble("latitude", loc.latitude);
        bundle.putDouble("longitude", loc.longitude);
        Log.v("test", "lat: " + loc.latitude + " -  long: " + loc.longitude);
        bundle.putInt("localisation", localisation);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    
    @Override
    public void onLocationChanged(final Location location) {
        // TODO Auto-generated method stub
        
    }

    
    
    
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		savedInstanceState.putString( "searchKeys", searchKeys); 
		//Log.v("test","key words after turn phone    searchKeys"+searchKeys);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	
	

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		  searchKeys = savedInstanceState.getString("searchKeys");  
	}
	
	
	
    
}
