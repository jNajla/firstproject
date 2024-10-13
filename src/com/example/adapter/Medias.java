package com.example.adapter;

public class Medias {
    int idMedia;
    long idPointPublique;
    String nomImage;
    
    public Medias(final long idPointPublique, final String nomImage) {
        super();
        this.idPointPublique = idPointPublique;
        this.nomImage = nomImage;
    }
    
    public int getIdMedia() {
        return idMedia;
    }
    
    public void setIdMedia(final int idMedia) {
        this.idMedia = idMedia;
    }
    
    public long getIdPointPublique() {
        return idPointPublique;
    }
    
    public void setIdPointPublique(final long idPointPublique) {
        this.idPointPublique = idPointPublique;
    }
    
    public String getNomImage() {
        return nomImage;
    }
    
    public void setNomImage(final String nomImage) {
        this.nomImage = nomImage;
    }
    
}
