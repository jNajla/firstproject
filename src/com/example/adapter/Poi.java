package com.example.adapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Poi implements Serializable {

	private static final long serialVersionUID = -5527566248002296042L;

	private String id;
	/**
	 * chaque poi a un nom
	 */
	private String name;

	/**
	 * latitude/longitude
	 */
	private double[] location;
	/**
	 * description
	 */
	private String description;
	/**
	 * addresse
	 */
	private String address;

	private String contactId;
	/**
	 * telephone d'accÃ¨s
	 */
	private String phone;
	/**
	 * fax du poi
	 */
	private String fax;
	/**
	 * addresse mail du poi
	 */
	private String email;
	/**
	 * site internet du poi
	 */
	private String webSite;
	/**
	 * date d'ouverture
	 */
	private String openingTime;
	/**
	 * date de fermeture
	 */
	private String closingTime;
	/**
	 * pour la rechercher uniquement
	 */
	private List<String> keyWords;
	/**
	 * Liste des Images
	 */
	private List<Image> imgs;

	private Date creationDate;

	private Date updateDate;

	private String createdBy;

	private String modifiedBy;

	private boolean enabled = false;

	public Poi() {
		this.creationDate = new Date();
		this.updateDate = new Date();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<String> getKeyWords() {
		return keyWords;
	}

	public void setListTitles(final List<String> listKeywords) {
		keyWords = listKeywords;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(final String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(final String webSite) {
		this.webSite = webSite;
	}

	public String getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(final String openingTime) {
		this.openingTime = openingTime;
	}

	public String getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(final String closingTime) {
		this.closingTime = closingTime;
	}

 public List<Image> getImgs() {
		return imgs;
	}

	public void setImgs(List<Image> imgs) {
		this.imgs = imgs;
	}

	public void setKeyWords(List<String> keyWords) {
		this.keyWords = keyWords;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	public static void main(String[] args) throws Exception {
		final Map<String, String> equivalents = new HashMap<String, String>() {
			{
				put("AIRF", "aerodrome");
				put("AIRP", "aeroport");
				put("AIRQ", "aerodrome abandonne");
				put("ANS", "site archeologiques");
				put("AREA", "zone");
				put("BLDG", "batiment");
				put("BNK", "banque");
				put("CMTY", "cimetiere");
				put("CTRR", "centre religieux");
				put("EST", "immobilier");
				put("FRM", "ferme");
				put("FRMS", "ferme");
				put("FRMT", "ferme");
				put("FRST", "foret");
				put("FT", "fort");
				put("GDN", "jardin");
				put("HBR", "port");
				put("HSE", "maison");
				put("HSP", "hopital");
				put("HTL", "hotel");
				put("HUT", "hutte");
				put("LK", "lac");
				put("LKI", "lac");
				put("LTHSE", "phare");
				put("MKT", "marche");
				put("ML", "moulin");
				put("MLM", "traitement minerai");
				put("MLO", "moulin olive");
				put("MLWND", "moulin vent");
				put("MN", "mine");
				put("MNMT", "monument");
				put("MSQE", "mosquee");
				put("OAS", "oasis");
				put("PAL", "palis");
				put("POOL", "piscine");
				put("PPLH", "lieu historique");
				put("PRK", "parc");
				put("PRT", "port");
				put("RSTN", "gare");
				put("SHRN", "tombeau");
				put("ST", "rue");
				put("TMB", "tombe");
				put("TOWR", "tour");
			}
		};
		Set<String> names = new HashSet<String>();
		
		FileReader fr = new FileReader(
				"/Users/ngasri/homework/revomon/projects/ws/ayn-server/src/main/resources/geonames_tn_filtered.csv");
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		int nbCall = 1;
		int limit = 2300;
		while ((line = br.readLine()) != null) {
			if (nbCall > limit) {
				break;
			}
			nbCall++;
			String[] strings = line.trim().split(";");
			internalCall(strings[1], strings[2],"/Users/ngasri/homework/revomon/projects/ws/ayn-server/src/test/resources/data2/"+strings[0]+".json");

		}
	}

	protected static String internalCall(final String lt, final String lg, String outputFile)
			throws IOException {
		String googleMapsUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&sensor=false";
		System.out.println(googleMapsUrl +"=>"+outputFile);
		final URL url = new URL(String.format(googleMapsUrl,lt,lg));
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.connect();
		final int code = urlConnection.getResponseCode();
		InputStream in = null;
		if (code != 200) {
			System.out.println("Bad response code : "+code);
			in = urlConnection.getErrorStream();
		}else{
			in = urlConnection.getInputStream();		
		}
		final BufferedReader br = new BufferedReader(new InputStreamReader(in));
		final StringBuffer jsonResponse = new StringBuffer();
		String line = null;
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);
		while ((line = br.readLine()) != null) {
			jsonResponse.append(line + "\n");
			bw.write(line+"\n");
		}
		//System.out.println("jsonResponse : "+ jsonResponse);
		br.close();
		bw.close();
		urlConnection.disconnect();

		return jsonResponse.toString();
	}
	

	//convertir l'objet poi a un objet json
	public JSONObject toJSON(){

	    JSONObject jsonObject= new JSONObject();
	    try {
	        jsonObject.put("id", getId());
	        jsonObject.put("name", getName());
	        jsonObject.put("location", getLocation());
	        jsonObject.put("description", getDescription());
	        jsonObject.put("address", getAddress());
	        jsonObject.put("contactId", getContactId());
	        jsonObject.put("phone", getPhone());
	        jsonObject.put("fax", getFax());
	        jsonObject.put("email", getEmail());
	        jsonObject.put("webSite", getWebSite());
	        JSONArray imgsArray = new JSONArray();
	        for(Image img : imgs){
	        	imgsArray.put(img.toJSON());
	        }
	        jsonObject.put("imgs",imgsArray);
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }

	    
	  // map:{clé:valeur,...}  pas de repitition dans une map
	    /*{
	    	"name":"POI-8131",
	    	"location":[36.51563517572016,10.594270531781575],
	    	"description":null,
	    	"address":"16 rue numero 72 tunis",
	    	"contactId":null,
	    	"phone":null,
	    	"fax":null,
	    	"email":null,
	    	"webSite":null,
	    	"openingTime":null,
	    	"closingTime":null,
	    	"keyWords":["restaurant","parc","administation","poste","station metro"],
	    	"imgs":[
	    	        {
	    	        	 "decription" : null,
	    	        	"name":null,
	    	        	"uri":null,
	    	        	 "from":null,
	    	        	 "to":null
	    	        },{
	    	        	 "decription" : null,
	    	        	"name":null,
	    	        	"uri":null,
	    	        	 "from":null,
	    	        	 "to":null
	    	        }
	    	   ],
	    	"creationDate":null,
	    	"updateDate":null,
	    	"createdBy":null,
	    	"modifiedBy":null,
	    	"enabled":true*/
	    	return jsonObject;
	    }
		
}
