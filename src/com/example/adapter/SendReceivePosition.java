package com.example.adapter;

public class SendReceivePosition {
    int id;
    Double longitude;
    Double latitude;
    String myPhoneNumber;
    String phoneNumber;
    
    public SendReceivePosition(final Double longitude, final Double latitude, final String myPhoneNumber,
                               final String phoneNumber) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
        this.myPhoneNumber = myPhoneNumber;
        this.phoneNumber = phoneNumber;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }
    
    public String getMyPhoneNumber() {
        return myPhoneNumber;
    }
    
    public void setMyPhoneNumber(final String myPhoneNumber) {
        this.myPhoneNumber = myPhoneNumber;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
}
