package com.example.locof;

public class Member {
    private String Dname,Latitude,Longitude,AddressLocale,Requests,date;
    public Member() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDname() {
        return Dname;
    }

    public void setDname(String dname) {
        Dname = dname;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getAddressLocale() {
        return AddressLocale;
    }

    public void setAddressLocale(String addressLocale) {
        AddressLocale = addressLocale;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getRequests() {
        return Requests;
    }

    public void setRequests(String requests) {
        Requests = requests;
    }
}
