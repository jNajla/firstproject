package com.example.business;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.adapter.Medias;
import com.example.adapter.MotCLE;
import com.example.adapter.PointInteretPrive;
import com.example.adapter.PointInteretPublique;
import com.example.adapter.ReceiveMessage;
import com.example.adapter.SendReceivePosition;
import com.google.android.gms.maps.model.LatLng;

public class DbManager extends Activity {
    long id1, id2;
    public SQLiteDatabase mydbprive = null;
    public SQLiteDatabase mydbpublique = null;
    boolean tableEmpty = false;
    // les tables
    public static final String POINTPRIVE_TABLE_NAME = "PointInteretPrive";
    public static final String POINTPUBLIQUE_TABLE_NAME = "PointInteretPublique";
    public static final String MOTCLE_TABLE_NAME = "Motcle";
    public static final String ASSOC_TABLE_NAME = "Association";
    public static final String MEDIA_TABLE_NAME = "Medias";
    public static final String SENDRECEIVEPOSITION_TABLE_NAME = "SendReceivePosition";
    public static final String LOCATION_FROM_SMS_TABLE = "locationFromSMS";
    
    // Description des colonnes de la table point interet publique
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOM = "NOM";
    public static final String COLUMN_TEL = "TEL";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_FAX = "FAX";
    public static final String COLUMN_SITEWEB = "SITEWEB";
    public static final String COLUMN_LATITUDE = "LATITUDE";
    public static final String COLUMN_LONGITUDE = "LONGITUDE";
    
    // Description des colonnes de la table mot cl�
    public static final String COLUMN_MOTCLE_ID = "ID_MOTCLE";
    public static final String COLUMN_LIBELE = "LIBELE";
    
    // Description des colonnes de la table association
    public static final String COLUMN_ASSOC_ID = "ID_ASSOC";
    public static final String COLUMN_ID_ENSEIGNE = "ID_ENSEIGNE";
    public static final String COLUMN_ID_MOTCLE = "ID_MOTCLE";
    
    // Description des colonnes de la table point d'interet priv�
    public static final String COLUMN_IDPRIVE = "ID";
    public static final String COLUMN_NOMPRIVE = "NOM";
    public static final String COLUMN_LATITUDEPRIVE = "LATITUDE";
    public static final String COLUMN_LONGITUDEPRIVE = "LONGITUDE";
    
    // Description des colonnes de la table Medias
    public static final String COLUMN_IDMEDIA = "IDMEDIA";
    public static final String COLUMN_IDPOINTPUBLIQUE = "IDPOINTPUBLIQUE";
    public static final String COLUMN_NOMIMAGE = "NOMIMAGE";
    
    // Description des colonnes de la table SEND RECEIVE POSITION
    public static final String COLUMN_IDSENDRECEIVE = "ID";
    public static final String COLUMN_PHONENUMBER = "PHONENUMBER";
    public static final String COLUMN_MYPHONENUMBER = "MYPHONENUMBER";
    public static final String COLUMN_LATITUDESENDRECEIVE = "LATITUDE";
    public static final String COLUMN_LONGITUDESENDRECEIVE = "LONGITUDE";
    
    // Description des colonnes de la table receive message
    public static final String COLUMN_IDRECEIVEMESSAGE = "ID";
    public static final String COLUMN_LATITUDE_RECEIVE = "LATITUDE";
    public static final String COLUMN_LONGITUDE_RECEIVE = "LONGITUDE";
    
    // SEND RECEIVE POSITION
    private static final String REQUETE_CREATION_SENDRECEIVEPOSITION = "CREATE TABLE if not exists "
                                                                       + SENDRECEIVEPOSITION_TABLE_NAME + " ("
                                                                       + COLUMN_IDSENDRECEIVE
                                                                       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                                       + COLUMN_PHONENUMBER + " LONG , "
                                                                       + COLUMN_MYPHONENUMBER + " LONG , "
                                                                       + COLUMN_LATITUDESENDRECEIVE + " DOUBLE , "
                                                                       + COLUMN_LONGITUDESENDRECEIVE + " DOUBLE );";
    
    // point d'interet publique
    private static final String REQUETE_CREATION_BDDPUBLIQUE = "CREATE TABLE if not exists " + POINTPUBLIQUE_TABLE_NAME
                                                               + " (" + COLUMN_ID
                                                               + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NOM
                                                               + " TEXT , " + COLUMN_TEL + " INTEGER , "
                                                               + COLUMN_DESCRIPTION + " TEXT , " + COLUMN_FAX
                                                               + " INTEGER , " + COLUMN_SITEWEB + " TEXT , "
                                                               + COLUMN_LATITUDE + " DOUBLE , " + COLUMN_LONGITUDE
                                                               + " DOUBLE  );";
    
    private static final String REQUETE_CREATION_TABLE_MOTCLE = "CREATE TABLE if not exists " + MOTCLE_TABLE_NAME
                                                                + " (" + COLUMN_MOTCLE_ID
                                                                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_LIBELE
                                                                + " TEXT );";
    
    private static final String REQUETE_CREATION_TABLE_ASSOCIATION = "CREATE TABLE if not exists " + ASSOC_TABLE_NAME
                                                                     + " (" + COLUMN_ASSOC_ID
                                                                     + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                                     + COLUMN_ID_ENSEIGNE + " INTEGER,"
                                                                     + COLUMN_ID_MOTCLE + " INTEGER,"
                                                                     + " FOREIGN KEY (" + COLUMN_ID_ENSEIGNE
                                                                     + ") REFERENCES " + POINTPUBLIQUE_TABLE_NAME
                                                                     + " (" + COLUMN_ID + "), " + " FOREIGN KEY ("
                                                                     + COLUMN_ID_MOTCLE + ") REFERENCES "
                                                                     + MOTCLE_TABLE_NAME + " (" + COLUMN_MOTCLE_ID
                                                                     + "));";
    // point d'interet priv�
    private static final String REQUETE_CREATION_BDDPRIVE = "CREATE TABLE if not exists " + POINTPRIVE_TABLE_NAME
                                                            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                            + COLUMN_NOM + " TEXT , " + COLUMN_LATITUDE + " DOUBLE , "
                                                            + COLUMN_LONGITUDE + " DOUBLE );";
    private static final String REQUETE_CREATION_TABLE_MEDIA = "CREATE TABLE if not exists " + MEDIA_TABLE_NAME + " ("
                                                               + COLUMN_IDMEDIA + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                               + COLUMN_IDPOINTPUBLIQUE + " long," + COLUMN_NOMIMAGE
                                                               + " TEXT );";
    
    private static final String REQUETE_CREATION_TABLE_RECEIVEMESSAGE = "CREATE TABLE if not exists "
                                                                        + LOCATION_FROM_SMS_TABLE + " ("
                                                                        + COLUMN_IDRECEIVEMESSAGE
                                                                        + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                                        + COLUMN_LATITUDE_RECEIVE + " DOUBLE, "
                                                                        + COLUMN_LONGITUDE_RECEIVE + " DOUBLE  );";
    
    public DbManager(final Context context) {
        try {
            // SUPPORT CARTE MEMOIRE
            final String dir = Environment.getExternalStorageDirectory().getPath();
            
            /* mydbprive = context
                .openOrCreateDatabase(dir + File.separator + "databaseprive", Context.MODE_PRIVATE, null);
            mydbpublique = context.openOrCreateDatabase(dir + File.separator + "databasepublique",
                                                        Context.MODE_PRIVATE, null);*/
            
            mydbprive = context.openOrCreateDatabase("databaseprive", Context.MODE_PRIVATE, null);
            mydbpublique = context.openOrCreateDatabase("databasepublique", Context.MODE_PRIVATE, null);
            
            mydbprive.execSQL(REQUETE_CREATION_BDDPRIVE);
            mydbpublique.execSQL(REQUETE_CREATION_BDDPUBLIQUE);
            mydbpublique.execSQL(REQUETE_CREATION_TABLE_MOTCLE);
            mydbpublique.execSQL(REQUETE_CREATION_TABLE_ASSOCIATION);
            mydbpublique.execSQL(REQUETE_CREATION_TABLE_MEDIA);
            mydbpublique.execSQL(REQUETE_CREATION_SENDRECEIVEPOSITION);
            mydbpublique.execSQL(REQUETE_CREATION_TABLE_RECEIVEMESSAGE);
            
        } catch (final Exception e) {
            final String err = e.getMessage() == null ? "Database error" : e.getMessage();
            Log.e("Database Error", err);
        }
    }
    
    public void AddEntrySendReceivePosition(final SendReceivePosition entite) {
        
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PHONENUMBER, entite.getPhoneNumber());
        contentValues.put(COLUMN_MYPHONENUMBER, entite.getMyPhoneNumber());
        contentValues.put(COLUMN_LATITUDESENDRECEIVE, entite.getLatitude());
        contentValues.put(COLUMN_LONGITUDESENDRECEIVE, entite.getLongitude());
        mydbpublique.insert(SENDRECEIVEPOSITION_TABLE_NAME, null, contentValues);
        
    }
    
    public void AddEntryPointPrive(final PointInteretPrive entite) {
        
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOMPRIVE, entite.getNom());
        contentValues.put(COLUMN_LATITUDEPRIVE, entite.getLatitude());
        contentValues.put(COLUMN_LONGITUDEPRIVE, entite.getLongitude());
        mydbprive.insert(POINTPRIVE_TABLE_NAME, null, contentValues);
        
    }
    
    public void AddEntryEnseigne(final PointInteretPublique entite) {
        
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOM, entite.getNom());
        contentValues.put(COLUMN_TEL, entite.getTel());
        contentValues.put(COLUMN_DESCRIPTION, entite.getDescription());
        contentValues.put(COLUMN_FAX, entite.getFax());
        contentValues.put(COLUMN_SITEWEB, entite.getSiteweb());
        contentValues.put(COLUMN_LATITUDE, entite.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, entite.getLongitude());
        mydbpublique.insert(POINTPUBLIQUE_TABLE_NAME, null, contentValues);
        
    }
    
    public void AddEntrymotcle(final MotCLE entite) {
        
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LIBELE, entite.getLibele());
        mydbpublique.insert(MOTCLE_TABLE_NAME, null, contentValues);
        
    }
    
    public void AddEntryMedia(final Medias entite1) {
        
        
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IDPOINTPUBLIQUE, id1);
        contentValues.put(COLUMN_NOMIMAGE, entite1.getNomImage());
        mydbpublique.insert(MEDIA_TABLE_NAME, null, contentValues);
    }
    
    public void AddEntryReceiveMessage(final ReceiveMessage entite) {
        
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDE_RECEIVE, entite.getLatitude());
        contentValues.put(COLUMN_LONGITUDE_RECEIVE, entite.getLongitude());
        mydbpublique.insert(LOCATION_FROM_SMS_TABLE, null, contentValues);
        
    }
    
    private boolean istableEmpty(final String tableName) {
        final String sqlQuery = "SELECT count(*) FROM " + tableName;
        final Cursor cursor = mydbpublique.rawQuery(sqlQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {
                // Empty
                tableEmpty = true;
            } else {
                tableEmpty = false;
            }
        }
        return tableEmpty;
    }
    
    public boolean isLocationFromSmsTableEmpty() {
        return istableEmpty(LOCATION_FROM_SMS_TABLE);
    }
    
    // read SMS table and get latitude
    // return: "latitude" or "0"
    public double getLatitudeFromSmsTable() {
        double latitude = 0;
        final String query1 = "SELECT LATITUDE from " + LOCATION_FROM_SMS_TABLE;
        final Cursor c = mydbpublique.rawQuery(query1, null);
        if (c != null && c.moveToFirst()) {
            latitude = c.getDouble(c.getColumnIndex(COLUMN_LATITUDE_RECEIVE));
        }
        return latitude;
        
    }
    
    // read SMS table and get longitude
    // return: "latitude" or "0"
    public double getLongitudeFromSmsTable() {
        double longitude = 0;
        final String query1 = "SELECT LONGITUDE from " + LOCATION_FROM_SMS_TABLE;
        final Cursor c = mydbpublique.rawQuery(query1, null);
        if (c != null && c.moveToFirst()) {
            longitude = c.getDouble(c.getColumnIndex(COLUMN_LONGITUDE_RECEIVE));
        }
        return longitude;
    }
    
    // read SMS table and get Location
    // return: "LatLng" or "0"
    public LatLng getLocationFromSmsTable() {
        final double latitude = getLatitudeFromSmsTable();
        final double longitude = getLongitudeFromSmsTable();
        Log.v("test", "db lat: " + latitude + " -  long: " + longitude);
        
        if (latitude != 0) {
            return new LatLng(latitude, longitude);
        } else {
            return null;
        }
        
    }
    
    public int deleteSmsTable() {
        return mydbpublique.delete(LOCATION_FROM_SMS_TABLE, null, null);
    }
    
    /*public void getName(){
        
        String name = null ;
        String query1 = "SELECT NOM from PointInteretPrive ";
        Cursor c = mydbprive.rawQuery(query1, null);
        if (c != null && c.moveToFirst()) {     
                c.getString(c.getColumnIndex(COLUMN_NOMPRIVE));
        
                Log.v("test","name:   "+c.getString(c.getColumnIndex(COLUMN_NOMPRIVE)));

        }
        
    }*/
    /*public String[] getItems(String KEY_ITEM) {
        String selectQuery = "SELECT " + "*" + " FROM " + POINTPRIVE_TABLE_NAME;
       
        Cursor cursor = mydbprive.rawQuery(selectQuery, null);
        String[] items = new String[cursor.getCount()];
        int i=0;
        if(cursor.moveToFirst()){
                do {
                        
                        String item = cursor.getString(cursor.getColumnIndex(KEY_ITEM));
                            items[i] = item;
                            i++;
                        
                } while (cursor.moveToNext());
        }
        
        cursor.close();
        
        return items;
    }*/
    public long lastId1() {
        final String query1 = "SELECT ID from PointInteretPublique order by ID DESC limit 1";
        final Cursor c = mydbpublique.rawQuery(query1, null);
        if (c != null && c.moveToFirst()) {
            id1 = c.getLong(c.getColumnIndex(COLUMN_ID));
            
        }
        return id1;
    }
    
    public long lastId2() {
        final String query2 = "SELECT ID_MOTCLE from Motcle order by ID_MOTCLE DESC limit 1";
        final Cursor c = mydbpublique.rawQuery(query2, null);
        if (c != null && c.moveToFirst()) {
            id2 = c.getLong(c.getColumnIndex(COLUMN_MOTCLE_ID));
            
        }
        return id2;
    }
    
    public void AddEntryAssoc(final long id1, final long id2) {
        
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID_ENSEIGNE, id1);
        contentValues.put(COLUMN_ID_MOTCLE, id2);
        mydbpublique.insert(ASSOC_TABLE_NAME, null, contentValues);
        
    }
    
   /* public void UpdateEntry(final PointInteretPublique entite) {
        final String where = "id=?";
        final String[] whereArgs = new String[] {
            String.valueOf(entite.getId())
        };
        final ContentValues dataToUpdate = new ContentValues();
        dataToUpdate.put(COLUMN_TEL, entite.getTel());
        dataToUpdate.put(COLUMN_DESCRIPTION, entite.getDescription());
        dataToUpdate.put(COLUMN_FAX, entite.getFax());
        dataToUpdate.put(COLUMN_SITEWEB, entite.getSiteweb());
        dataToUpdate.put(COLUMN_LATITUDE, entite.getLatitude());
        dataToUpdate.put(COLUMN_LONGITUDE, entite.getLongitude());
        try {
            mydbpublique.update(POINTPUBLIQUE_TABLE_NAME, dataToUpdate, where, whereArgs);
        } catch (final Exception e) {
            e.getMessage().toString();
        }
    }*/
    
    public List<PointInteretPrive> GetAll() {
      
        Cursor cursor = null;
        // R�cup�ration de la liste des points d'interet priv�
        if (mydbprive != null) {
            cursor = mydbprive.query(POINTPRIVE_TABLE_NAME, new String[] {
                COLUMN_IDPRIVE, COLUMN_NOMPRIVE, COLUMN_LATITUDEPRIVE, COLUMN_LONGITUDEPRIVE
            }, null, null, null, null, null);
        } else {
            Log.v("test", "mydb null");
        }
        return ConvertCursorToListObject(cursor);
        
    }
    
    private List<PointInteretPrive> ConvertCursorToListObject(final Cursor c) {
        
        final List<PointInteretPrive> liste = new ArrayList<PointInteretPrive>();
        // Si la liste est vide
        if (c.getCount() == 0) {
            return liste;
        }
        
        // position sur le premier item
        c.moveToFirst();
        
        // Pour chaque item
        do {
            
            final PointInteretPrive enseigne = ConvertCursorToObject(c);
            liste.add(enseigne);
        } while (c.moveToNext());
        
        // Fermeture
        c.close();
        
        return liste;
    }
    
    private PointInteretPrive ConvertCursorToObject(final Cursor c) {
        String name;
        int id;
        double lat, lon;
        
        name = null;
        
        lat = lon = 0;
        id = 0;
        if (!c.isNull(c.getColumnIndex(COLUMN_IDPRIVE))) {
            id = c.getInt(c.getColumnIndex(COLUMN_IDPRIVE));
        }
        Log.v("test", "affiche" + id);
        
        if (!c.isNull(c.getColumnIndex(COLUMN_NOMPRIVE))) {
            name = c.getString(c.getColumnIndex(COLUMN_NOMPRIVE));
        }
        Log.v("test", "affiche" + name);
        
        if (!c.isNull(c.getColumnIndex(COLUMN_LATITUDEPRIVE))) {
            lat = c.getDouble(c.getColumnIndex(COLUMN_LATITUDEPRIVE));
        }
        Log.v("test", "affiche" + lat);
        
        if (!c.isNull(c.getColumnIndex(COLUMN_LONGITUDEPRIVE))) {
            lon = c.getDouble(c.getColumnIndex(COLUMN_LONGITUDEPRIVE));
        }
        Log.v("test", "affiche" + lon);
        
        final PointInteretPrive enseigne = new PointInteretPrive(name, lat, lon);
        return enseigne;
        
    }
    
    public void closeMydb() {
        mydbpublique.close();
        mydbprive.close();
    }
}
