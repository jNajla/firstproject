package com.example.appayn;

public class SearchQuery {

	private String[] keywords;
	private double latitude;
	private double longitude;
	private int rayonOdSearch;
	public SearchQuery(){
		
	}
	public SearchQuery(String[] keywords, double latitude, double longitude,
			int rayonOdSearch) {
		super();
		this.keywords = keywords;
		this.latitude = latitude;
		this.longitude = longitude;
		this.rayonOdSearch = rayonOdSearch;
	}
	public String[] getKeywords() {
		return keywords;
	}
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getRayonOdSearch() {
		return rayonOdSearch;
	}
	public void setRayonOdSearch(int rayonOdSearch) {
		this.rayonOdSearch = rayonOdSearch;
	}
	
	
}
