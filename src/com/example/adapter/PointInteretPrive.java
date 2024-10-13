package com.example.adapter;

public class PointInteretPrive {
    int id;
    double longitude, latitude;
    String nom;
    
    public PointInteretPrive(final String nom, final double latitude, final double longitude) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
        this.nom = nom;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(final String nom) {
        this.nom = nom;
    }
    
}
