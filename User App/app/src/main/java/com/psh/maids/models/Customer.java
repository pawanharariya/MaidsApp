package com.psh.maids.models;

public class Customer {
    private String name, mobile, city, imageUrl, locality;

    public Customer() {
    }

    public Customer(String name, String mobile, String city, String locality, String imageUrl) {
        this.name = name;
        this.mobile = mobile;
        this.city = city;
        this.locality = locality;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
