package com.example.adapter;

public class PointInteretPublique {
    private int id;
    private String nom;
    private int tel;
    private String description;
    private int fax;
    private String siteweb;
    private Double latitude;
    private Double longitude;
    
    public PointInteretPublique() {
        super();
    }
    
    public PointInteretPublique(final int id, final String nom, final int tel, final String description, final int fax,
                                final String siteweb, final Double latitude, final Double longitude) {
        
        this.nom = nom;
        this.tel = tel;
        this.description = description;
        this.fax = fax;
        this.siteweb = siteweb;
        this.latitude = latitude;
        this.longitude = longitude;
        
        if (id >= 0) {
            this.id = id;
        }
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(final String nom) {
        this.nom = nom;
    }
    
    public int getTel() {
        return tel;
    }
    
    public void setTel(final int tel) {
        this.tel = tel;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public int getFax() {
        return fax;
    }
    
    public void setFax(final int fax) {
        this.fax = fax;
    }
    
    public String getSiteweb() {
        return siteweb;
    }
    
    public void setSiteweb(final String siteweb) {
        this.siteweb = siteweb;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }
    
}
