package com.example.adapter;

import java.io.File;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;

public class Image {

	private File file;

	private String decription;

	private String name;

	private String uri;

	private Date from;

	private Date to;

	private boolean enabled = false;

	public String getDecription() {
		return decription;
	}

	public void setDecription(String decription) {
		this.decription = decription;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	// convertir l'objet image a un objet json
	public JSONObject toJSON() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("from", getFrom());
			jsonObject.put("name", getName());
			jsonObject.put("to", getTo());
			jsonObject.put("description", getDecription());
			jsonObject.put("uri", getUri());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	@JsonIgnore
	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}