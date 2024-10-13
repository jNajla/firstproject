package com.revomon.ayn.poi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.entity.ByteArrayEntity;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.example.adapter.Image;
import com.example.adapter.Poi;



public class PoiService {

	private static final String POI_CREATE = "http://ks3278614.kimsufi.com:8080/ayn-server/pois/";
	private static final String IMAGE_CREATE = "http://ks3278614.kimsufi.com:8080/ayn-server/pois/%s/images";
	private static PoiService singleton = new PoiService();
	private static ObjectMapper objectMapper;
	DefaultHttpClient httpclient;
	private PoiService(){
		httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        //transforme json--> object et vise vers ça
        objectMapper = new ObjectMapper();
	}
	public static PoiService getInstance(){
		return singleton ;
	}
	public void create(Poi poi) throws Exception {
		 HttpPut httput = new HttpPut(POI_CREATE);
		  
		  
		  List<Image> imgs = poi.getImgs();
		  poi.setImgs(null);
		  
		  httput.setHeader("Content-Type", "application/json");
		  HttpEntity entity = new ByteArrayEntity(objectMapper.writeValueAsBytes(poi));
		  httput.setEntity(entity);
		        HttpResponse response = httpclient.execute(httput);
		        HttpEntity result = response.getEntity();
		        int statusCode = response.getStatusLine().getStatusCode();
		        if(statusCode==200){
		        final InputStream instream = result.getContent();
		        if (instream != null) {
		            final BufferedReader br = new BufferedReader(new InputStreamReader(instream));
		            final StringBuilder jsonResponse = new StringBuilder();
		            String line = null;
		            while ((line = br.readLine()) != null) {
		                jsonResponse.append(line + "\n");
		            }
		            String poiId = jsonResponse.toString();

		            System.out.println("le poi creer  c'est :"+poiId);
		            poi.setId(poiId);
		            if(!imgs.isEmpty()){
		             MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		             for (Image image : imgs) {
		              multipartEntity.addPart("file-data", new FileBody( image.getFile() ,"image/jpeg"));
		              multipartEntity.addPart("img-data", new StringBody( objectMapper.writeValueAsString(image) , "application/json", Charset.forName("UTF-8")));    
		    }
		             HttpPost httpPost = new HttpPost(String.format(IMAGE_CREATE, poiId));

		             httpPost.setEntity(multipartEntity);
		          HttpResponse imgCreate = httpclient.execute(httpPost);
		          System.out.println(imgCreate.getStatusLine());
		          HttpEntity resEntity = imgCreate.getEntity();
		          if (resEntity  != null) {
		           System.out.println(EntityUtils.toString(resEntity));
		          }
		          if (resEntity != null) {
		           resEntity.consumeContent();
		          }
		            }
		        }        
		  
		        }else{
		         //log
		        }
    }

}