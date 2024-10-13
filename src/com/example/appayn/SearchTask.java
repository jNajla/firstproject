package com.example.appayn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.adapter.Poi;

public class SearchTask extends AsyncTask<SearchQuery, Integer, List<Poi>> {
	/**
	 * CONSTANTES. /
	 *******************************************************/
	private static final String TOAST_MSG = "RECHERCHE EN COURS";
	private static final String TOAST_ERR_MAJ = "Pas des points d'interet dans cette region";

	/*******************************************************/
	/**
	 * ATTRIBUTS. /
	 *******************************************************/
	private final Context context;

	private String consumerUrl;

	private String consumerLogin;

	private String consumerPassword;

	private int connectTimeout = 5000;

	private int readTimeout = 3500;

	private boolean useProxy;

	private boolean needAuthent = false;

	private InetSocketAddress proxyAddress;

	private String proxyUsername;

	private String proxyPassword;

	private String proxyHost;

	private int proxyPort;

	/*******************************************************/
	/**
	 * METHODES / FONCTIONS. /
	 *******************************************************/
	/**
	 * Constructeur.
	 * 
	 */
	public SearchTask(final Context context, final boolean useProxy) {
		this.context = context;
		this.useProxy = useProxy;
		if (useProxy) {
			if (this.proxyHost == null || this.proxyHost.trim().length() <= 0) {
				throw new RuntimeException(
						"proxyhost cannot be null or empty when setting Proxy");
			}
			if (this.proxyAddress == null) {
				this.proxyAddress = new InetSocketAddress(this.proxyHost,
						this.proxyPort);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPreExecute() {
		Log.v("test", "Recherche en cours    :");

		Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();

	}

	/***
	 * {@inheritDoc}
	 */

	public String search(final String[] keywords, double latitude,
			double longitude, final int rayonDeRecherche) throws IOException {
		Log.v("test", "recherche ...");
		final StringBuilder keyword = new StringBuilder();
		for (final String string : keywords) {
			keyword.append(string).append(',');
		}
		// supprimer le dernier ,
		keyword.deleteCharAt(keyword.lastIndexOf(","));

		final String callUrl = String.format(this.consumerUrl, keyword,
				latitude, longitude, rayonDeRecherche);
		Log.v("test ", "callUrl " + callUrl);
		final URL url = new URL(callUrl);
		HttpURLConnection urlConnection = null;

		if (this.useProxy) {
			if (this.proxyUsername != null
					&& this.proxyUsername.trim().length() > 0) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(proxyUsername,
								proxyPassword.toCharArray());
					}
				});
			}
			final Proxy proxy = new Proxy(Proxy.Type.HTTP, this.proxyAddress);

			urlConnection = (HttpURLConnection) url.openConnection(proxy);

		} else {

			Log.v("test", "connection ...  ");
			urlConnection = (HttpURLConnection) url.openConnection();

		}
		urlConnection.setConnectTimeout(this.connectTimeout);
		//Log.e("test","timeout");
		urlConnection.setReadTimeout(this.readTimeout);
		if (this.needAuthent) {
			final String userpass = this.consumerLogin + ":"
					+ this.consumerPassword;
			final String basicAuth = "Basic "
					+ new String(Base64.encodeBase64(userpass.getBytes()));
			urlConnection.setRequestProperty("Authorization", basicAuth);
		}
		urlConnection.connect();
		Log.v("test", "cnnecté ...  ");
		final int code = urlConnection.getResponseCode();
		Log.v("test", "code : " + code + ", pour " + callUrl);

		if (code != 200) {
			Log.v("test", "Bad response code : " + code);
		  return null;
			//throw new RuntimeException("Bad response code : " + code);
			
			
		}
		

		final BufferedReader br = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));
		final StringBuilder jsonResponse = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonResponse.append(line + "\n");
		}

		// Log.v("test","response :"+jsonResponse);
		// System.out.println("jsonResponse : " + jsonResponse);
		br.close();

		Log.v("test", "deconnexion ... ");
		urlConnection.disconnect();
		return jsonResponse.toString();
	}

	@Override
	protected List<Poi> doInBackground(final SearchQuery... searchQueries) {
		SearchQuery searchQuery = searchQueries[0];
		setConsumerUrl("http://ks3278614.kimsufi.com:8080/ayn-server/pois/search/%s/lt/%s/lg/%s/distance/%d");
		setNeedAuthent(false);
		// parser resultat json
		//seachQuery:Enveloppe
		long startTime=System.currentTimeMillis();
		try {
		
			final String jsonResponse = search(searchQuery.getKeywords(),
					searchQuery.getLatitude(), searchQuery.getLongitude(),
					searchQuery.getRayonOdSearch());

			 Log.v("searchTask","response           :"+jsonResponse+"response after "+(System.currentTimeMillis()-startTime));
			 //convert result(string) to json
			JSONArray jsonObjects = new JSONArray(jsonResponse);
			//liste des pois
			List<Poi> pois = new ArrayList<Poi>();
			//parcourir l'objet json et remplir l'objet poi
			for (int i = 0; i < jsonObjects.length(); i++) {
				JSONObject jo = jsonObjects.getJSONObject(i);
				//créer un objet poi et ajouter a la liste des pois
				Poi poi = new Poi();
				pois.add(poi);
				poi.setId("id");
				poi.setName("name");
				poi.setDescription("description");
				poi.setContactId("contactId");
				poi.setFax("fax");
				poi.setPhone("phone");
				poi.setWebSite("webSite");
				poi.setEmail("email");
				poi.setAddress(jo.getString("address"));
				poi.setClosingTime(jo.getString("closingTime"));
				poi.setOpeningTime(jo.getString("openingTime"));
				JSONArray latlngArray = jo.getJSONArray("location");
				poi.setLocation(new double[] { latlngArray.getDouble(0),
						latlngArray.getDouble(1) });
				
				
				/*JSONArray keysWords = jo.getJSONArray("keyWords");
				List<String> KeyList = new ArrayList<String>();
				for (int j = 0; j < KeyList.size(); j++) {
				poi.setKeyWords(keysWords.);*/
				
				
				
				/*JSONArray imgArray = jo.getJSONArray("imgs");
				List<Image> imgList = new ArrayList<Image>();
				poi.setImgs(imgList);
				for (int j = 0; j < imgArray.length(); j++) {
					JSONObject imgJo = imgArray.getJSONObject(i);
					Image img = new Image();
					imgList.add(img);
					// setter
				}
				*/
				//mots clés ???
				
			}

			return pois;
		} catch (final IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		} catch (JSONException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}finally{
			Log.v("serchTask","finish task after "+(System.currentTimeMillis()-startTime));
		}

	}

	public String getConsumerUrl() {
		return consumerUrl;
	}

	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}

	public String getConsumerLogin() {
		return consumerLogin;
	}

	public void setConsumerLogin(String consumerLogin) {
		this.consumerLogin = consumerLogin;
	}

	public String getConsumerPassword() {
		return consumerPassword;
	}

	public void setConsumerPassword(String consumerPassword) {
		this.consumerPassword = consumerPassword;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public boolean isNeedAuthent() {
		return needAuthent;
	}

	public void setNeedAuthent(boolean needAuthent) {
		this.needAuthent = needAuthent;
	}

	public InetSocketAddress getProxyAddress() {
		return proxyAddress;
	}

	public void setProxyAddress(InetSocketAddress proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostExecute(final List<Poi> result) {
		if (result.isEmpty()) {
			Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();
			Log.v("test", "c pas bon :");
		} else {
			Log.v("test", "c bon   :");
			/*Toast.makeText(context, "recherche terminé", Toast.LENGTH_SHORT)
					.show();*/
		}
	}

}
