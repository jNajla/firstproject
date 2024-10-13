package com.example.appayn;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.Image;
import com.example.adapter.Medias;
import com.example.adapter.MotCLE;
import com.example.adapter.Poi;
import com.example.adapter.PointInteretPublique;
import com.example.business.DbManager;
import com.revomon.ayn.poi.PoiCreationTask;

public class PointPublique extends Activity implements LocationListener {
	int cle = 0;
	double latitude;
	double longitude;
	Location location;
	final static int CAMERA_RESULT = 0;
	boolean saveposition = false;
	List<Image> imgList = new ArrayList<Image>();
	RelativeLayout linear, linear2, linear21, linear22;
	EditText edit;
	int nb = 0;
	List<String> keyWords = new ArrayList<String>();

	long id1, id2, id;
	int VERTICAL = 1;
	String nom, description, tel, fax, siteweb, nomImage, mail, dateOuv,
			dateFerm, address;
	// int tel, fax;
	Button buttonAdd, buttonCancel, Ajoutmotcle;
	public DbManager dbb;
	ScrollView scroll;
	int pic = 1;
	String[] tabpic = new String[2000];
	boolean take = false;
	LocationManager locationManager;
	String[] tab = new String[1000000];
	ProgressDialog myPd_ring;

	EditText aaeditTextNomAdd;
	EditText edittel;
	EditText editfax;
	int idNom = 1, idDescription = 2, idTel = 3, idWebSite = 4, idFax = 5,
			idEmail = 6, idAdr = 7, idDateO = 8, idDateF = 9;
	TableLayout tableLayout, tableLayout2;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0;// 1000 * 0 * 1; // 1
														// minute
	Date dateMin ;
	Date dateMax  ;
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pointpublique);

		scroll = (ScrollView) findViewById(R.id.scrollView1);
		linear21 = (RelativeLayout) findViewById(R.id.linear21);
		linear22 = (RelativeLayout) findViewById(R.id.linear22);

		tableLayout = new TableLayout(this);
		final TableLayout.LayoutParams lp2 = new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tableLayout.setLayoutParams(lp2);

		// Ajout champs dynamiquement
		addNewRow("                                      ", -1);
		addNewRow("                                      ", -1);
		addNewRow("                                      ", -1);
		addNewRow("    Name        :                            ", idNom);
		addNewRow("    Description :                            ",
				idDescription);
		addNewRow("    Telephone   :                            ", idTel);
		addNewRow("    Site Web    :                            ", idWebSite);
		addNewRow("    Fax         :                            ", idFax);
		addNewRow("    Email       :                            ", idEmail);
		addNewRow("    Address     :                            ", idAdr);
		addNewRow("    Date d'ouverture :                            ", idDateO);
		addNewRow("    Date de fermeture :                            ",
				idDateF);
		linear21.addView(tableLayout);

		// 2--boutton cancel
		final Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();

			}
		});
		// 3--boutton prendre photo
		final Button Prendreunephoto = (Button) findViewById(R.id.Prendreunephoto);
		Prendreunephoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				TakePicture();

			}
		});

		// 1--boutton ajouter

		buttonAdd = (Button) findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				EditText date1 = (EditText) findViewById(idDateO);
				EditText date2 = (EditText) findViewById(idDateF);
				dateFerm = ((EditText) findViewById(idDateF)).getText().toString();
				dateOuv = ((EditText) findViewById(idDateO)).getText().toString();
				

				final String tel1 = ((EditText) findViewById(idTel)).getText()
						.toString();
				final String fax1 = ((EditText) findViewById(idFax)).getText()
						.toString();
				nom = ((EditText) findViewById(idNom)).getText().toString();

				aaeditTextNomAdd = (EditText) findViewById(idNom);
				editfax = (EditText) findViewById(idFax);
				edittel = (EditText) findViewById(idTel);
				EditText editEmail = (EditText) findViewById(idEmail);
				mail = ((EditText) findViewById(idEmail)).getText()
						.toString();
				if (nom.compareTo("") == 0) {

					aaeditTextNomAdd.setError("champ obligatoire");
					Log.v("test", "nom vide :" + nom);
					saveposition = false;
				} else {
					if (!isNumeric(tel1) && !tel1.equals("")) {

						edittel.setError("champ numerique");
						saveposition = false;
					} else {
						if (!isNumeric(fax1) && !fax1.equals("")) {
							editfax.setError("champ numerique");
							saveposition = false;
					}else{
						if(dates(dateOuv)==false && !dateOuv.equals("")  )
						{
						
							date1.setError("Format date: dd/MM/yyyy");
							
					}else{
						if(dates(dateFerm)==false && !dateFerm.equals(""))
						{
							
							date2.setError("Format date: dd/MM/yyyy");
						}else{
							if(isValidEmailAddress(mail)==false && !mail.equals("")){
								
								editEmail.setError("ceci est une adresse mail");
								saveposition = false;
						} else {
							date2.setError(null);
							date1.setError(null);
							aaeditTextNomAdd.setError(null);
							editfax.setError(null);
							edittel.setError(null);
							if (startGPS()) {
								saveposition = true;

								// remplir la table keyWords avec tous les cles
								int j;
								for (j = 0; j < cle; j++) {
									keyWords.add(((EditText) findViewById(j + 1000))
											.getText().toString());
									//Log.v("test","mots clés ::" + keyWords.get(j));
								}

								myPd_ring = ProgressDialog
										.show(PointPublique.this,
												"Enregistrer ma position",
												"veuillez attendre la reception de votre position..",
												true);
								myPd_ring.setCancelable(true);
								// myPd_ring.setCanceledOnTouchOutside(false);
							}
						}
					}

				}
			}
					}	}}	});

		// --boutton mot cle linear1

		Ajoutmotcle = (Button) findViewById(R.id.Ajoutmotcle);
		Ajoutmotcle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				try {
					AddChampMotCle();
				} catch (final Exception e) {
					Log.d("test",
							"Failed to create new edit text: " + e.toString());
				}

			}

		});

	}

	@SuppressLint("ResourceAsColor")
	void addNewRow(final String label, final int idField) {
		final TextView mytextView = new TextView(this);
		mytextView.setText(label);
		mytextView.setTextColor(R.color.orange);
		if (idField > 0) {
			mytextView.setPadding(2, 30, 2, 30);
		}
		final EditText eedit = new EditText(this);
		if (idField > 0) {
			eedit.setId(idField);
			eedit.setWidth(200);
			eedit.setHeight(33);
			eedit.setGravity(17); // 17= center
			eedit.setBackgroundColor(0xFFFFFFFF);
			// eedit.setText(textContent);
		}

		final TableRow.LayoutParams lp = new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		final TableRow tableRow = new TableRow(this);
		tableRow.setLayoutParams(lp);
		tableRow.addView(mytextView);
		if (idField > 0) {
			tableRow.addView(eedit);

		}
		tableLayout.addView(tableRow);

	}

	@SuppressLint("ResourceAsColor")
	void addNewRow2(final String label, final int idField,
			final String textContent, final boolean isNew) {
		final TextView mytextView = new TextView(this);
		mytextView.setText("   " + label + "                                 ");
		mytextView.setTextColor(R.color.orange);
		if (idField > 0) {
			mytextView.setPadding(2, 30, 2, 30);
		}
		final EditText eedit = new EditText(this);
		if (idField > 0) {
			eedit.setId(idField);
			eedit.setWidth(200);
			eedit.setHeight(33);
			eedit.setGravity(17); // 17= center
			eedit.setBackgroundColor(0xFFFFFFFF);
			eedit.setText(textContent);
			if (isNew) {
				//int size = keyWords.size();
				keyWords.add(cle, eedit.getText().toString());
				//keyWords.add(eedit.getText().toString());
				Log.v("test","cleee: "+cle);
			}
		}

		final TableRow.LayoutParams lp = new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		final TableRow tableRow = new TableRow(this);
		tableRow.setLayoutParams(lp);
		tableRow.addView(mytextView);
		if (idField > 0) {
			tableRow.addView(eedit);

		}
		tableLayout2.addView(tableRow);

	}
	public boolean isValidEmailAddress(String email) {
	       java.util.regex.Pattern p = java.util.regex.Pattern.compile(".+@.+\\.[a-z]+");
	       java.util.regex.Matcher m = p.matcher(email);
	       return m.matches();
	}

	private boolean dates(String date) {
		boolean isDate=false;
		Date parsedate;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			parsedate = (dateFormat.parse(date));
			dateMin = (dateFormat.parse("01/01/1500"));
			dateMax = new Date();
			if((parsedate.after(dateMin)) && (parsedate.before(dateMax))){
				isDate = true;
				Log.v("test", "isDate   " + isDate);
			}
			
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			isDate = false;
			Log.v("test", "isDate   " + isDate);
		}
		return isDate;
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

	public void TakePicture() {

		final String imageFilePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + nomImage;
		final File imageFile = new File(imageFilePath);
		Image img = new Image();
		img.setName(nomImage);
		img.setFile(imageFile);
		imgList.add(img);

		final Uri imageFileUri = Uri.fromFile(imageFile);
		final Intent i = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
		startActivityForResult(i, CAMERA_RESULT);

	}

	// l'activite appelante est presente pour la photo
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case CAMERA_RESULT:
			if (resultCode == RESULT_OK) {
				final String nomEnseigne = ((EditText) findViewById(idNom))
						.getText().toString();
				nomImage = nomEnseigne + pic + ".jpg";
				tabpic[pic] = nomImage;
				pic++;
				Log.v("test", ":nom de l'image" + nomImage);
				take = true;
			}

			break;

		}
	}

	@SuppressLint("NewApi")
	public void AddChampMotCle() {
		// table contient 2 colonnes
		final TableLayout.LayoutParams lp2 = new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tableLayout2 = new TableLayout(this);

		tableLayout2.setLayoutParams(lp2);

		// read all fileds
		int j;
		for (j = 0; j < cle; j++) {
			
			
			/*keyWords.add(((EditText) findViewById(j + 1000)).getText()
					.toString());*/
			keyWords.add(j, ((EditText) findViewById(j + 1000)).getText()
					.toString());
			/*Log.v("test", "edit ::" + ((EditText) findViewById(j + 1000)).getText()
			.toString());*/
			Log.v("test", "mots cle ::" + keyWords.get(j));
			
			//Log.v("test", "mots cle ::" + cle);
		}

		// remove all view
		linear22.removeAllViews();

		// insert existed fields
		int i;
		for (i = 0; i < cle; i++) {
			addNewRow2("Mot cle " + (i + 1) + ":", i + 1000, keyWords.get(i),
					false);
			Log.v("test", " insert existed fields ::" + keyWords.get(i));
			//Log.v("test", "nombre de clés::" + cle);
			//Log.v("test", "compteur ::" + i);
		}
		// add new field
		addNewRow2("Mot cle " + (cle + 1) + ":", cle + 1000, "", true);
		linear22.addView(tableLayout2);
		cle++;

	}

	@Override
	public void onLocationChanged(final Location location) {
		if (saveposition == true && location != null
				&& location.getAccuracy() < 100) {

			myPd_ring.dismiss();
			Log.v("test", "  :position will be saved  ");
			// recuperer la position GPS
			latitude = location.getLatitude();
			longitude = location.getLongitude();

			// get all data
			getAllDataPOI();

			// add all data to poi
			Poi poi = addDataToPOI(location);

			try {
				// PoiService.getInstance().create(poi);
				PoiCreationTask asyncTask = new PoiCreationTask(this);
				asyncTask.execute(poi);

			} catch (Exception e) {
				Log.e("test", "erreur lors de la creation de poi", e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// savePublicPoi(-1, nom, tel, description, fax, siteweb, latitude,
			// longitude, keyWords, tabpic);
			finish();

		}

	}

	public Poi addDataToPOI(Location loc) {
		// create poi object
		Poi poi = new Poi();

		// remplir le poi par les données.

		poi.setName(nom);
		poi.setDescription(description);
		poi.setWebSite(siteweb);
		poi.setEmail(mail);
		poi.setClosingTime(dateFerm);
		poi.setOpeningTime(dateOuv);
		poi.setAddress(address);
		poi.setFax(fax);
		poi.setPhone(tel);
		poi.setLocation(new double[] { loc.getLatitude(), loc.getLongitude() });
		poi.setEnabled(true);

		// poi.setId(id);
		// poi.setCreationDate();
		poi.setImgs(imgList);
		poi.setKeyWords(keyWords);

		return poi;
	}

	public boolean isNumeric(final String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public void getAllDataPOI() {
		/****************************** Get all data POI *********************************************/

		/*
		 * final String tel1 = ((EditText)
		 * findViewById(idTel)).getText().toString();
		 * 
		 * if (!tel1.equals("")) { tel = Integer.parseInt(tel1);
		 * 
		 * } else {
		 * 
		 * tel = 0; }
		 * 
		 * final String fax1 = ((EditText)
		 * findViewById(idFax)).getText().toString(); if (!fax1.equals("")) {
		 * fax = Integer.parseInt(fax1); } else { fax = 0; }
		 */
		nom = ((EditText) findViewById(idNom)).getText().toString();
		Log.v("test", "nom ::" + nom);
		description = ((EditText) findViewById(idDescription)).getText()
				.toString();
		Log.v("test", "description ::" + description);
		tel = ((EditText) findViewById(idTel)).getText().toString();
		Log.v("test", "tel ::" + tel);
		fax = ((EditText) findViewById(idFax)).getText().toString();
		Log.v("test", "fax ::" + fax);
		siteweb = ((EditText) findViewById(idWebSite)).getText().toString();
		Log.v("test", "siteweb ::" + siteweb);
		mail = ((EditText) findViewById(idEmail)).getText().toString();
		Log.v("test", "mail ::" + mail);
		dateOuv = ((EditText) findViewById(idDateO)).getText().toString();
		Log.v("test", "dateOuv ::" + dateOuv);
		dateFerm = ((EditText) findViewById(idDateF)).getText().toString();
		Log.v("test", "dateFerm ::" + dateFerm);
		address = ((EditText) findViewById(idAdr)).getText().toString();
		Log.v("test", "address ::" + address);
	}

	@SuppressLint("NewApi")
	public void savePublicPoi(final int idpoi, final String nom, final int tel,
			final String description, final int fax, final String siteweb,
			final Double latitude, final Double longitude,
			final String[] motscle, final String[] tabpic) {

		/****************************** creer la base de donnee si necessaire *********************************************/
		dbb = new DbManager(this);

		/****************************** last id POI *********************************************************************/
		id1 = dbb.lastId1();
		Log.v("test", "dernier id Enseigne  : " + id1);
		/****************************** last mot cle *********************************************************************/
		id2 = dbb.lastId2();
		Log.v("test", "dernier id mot cle  : " + id2);
		Log.e("test", "nb des mots cles : " + cle);

		/****************************** Add Entry POI ********************************************************************/
		dbb.AddEntryEnseigne(new PointInteretPublique(-1, nom, tel,
				description, fax, siteweb, latitude, longitude));
		id = dbb.lastId1();
		for (int j = 1; j < pic; j++) {
			if (take == true) {
				dbb.AddEntryMedia(new Medias(id, tabpic[j]));
			}
		}

		/****************************** Add keys words ********************************************************************/
		for (int i = 0; i < cle; i++) {
			Log.e("test", "mot cle : " + motscle[i]);
			if (keyWords.get(i).isEmpty()) {
				/*
				 * Toast.makeText(getBaseContext(), "mot clÃ© vide " + cle +
				 * " keyWords = " + keyWords[i], Toast.LENGTH_SHORT) .show();
				 */
			} else {
				dbb.AddEntrymotcle(new MotCLE(motscle[i]));
			}

		}

		/****************************** Add table association **************************************************************/
		id1 = id1 + 1;
		for (int count = 1; count <= cle; count++) {
			id2 = id2 + 1;
			Log.v("test", "ajouter id enseigne ici" + id1);
			Log.v("test", "ajouter id mot cle ici" + id2);
			dbb.AddEntryAssoc(id1, id2);

		}

		/****************************** Reinitialiser ********************************************************************/
		saveposition = false;

		/****************************** Toast ********************************************************************/
		final Toast toast = Toast.makeText(PointPublique.this,
				"Votre position est sauvegardee", Toast.LENGTH_SHORT);
		toast.show();

		/****************************** close mydb ********************************************************************/
		dbb.closeMydb();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		// fermer la boite de dialog pour eviter le crash
		if (myPd_ring != null) {
			myPd_ring.dismiss();
		}

		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}

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
	protected void onSaveInstanceState(Bundle outState) {
		//outState.putString("motCle", keyWords);
		//outState.putStringArrayList("keyWords", keyWords);
		
		Log.v("test", "key words after turn phone     motCle" + keyWords);
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
}
