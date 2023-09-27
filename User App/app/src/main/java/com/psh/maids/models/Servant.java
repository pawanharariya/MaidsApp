package com.psh.maids.models;

import java.io.Serializable;
import java.util.List;

public class Servant implements Serializable {

    private String id;
    private String name;
    private String nameOther;
    private String mobile;
    private String age;
    private String cost;
    private boolean availability;
    private List<String> category;
    private String imageUrl;
    private double rating;
    private String about;
    private String pincode;
    private String gender;
    private String address;

    //empty constructor required for firebase
    public Servant() {

    }

    public Servant(String firebaseId, String name, String nameOther, String age, String gender, String about, List<String> category, String cost, String imageUrl, String address, String mobile, String pincode, boolean availability) {
        this.id = firebaseId;
        this.name = name;
        this.nameOther = nameOther;
        this.age = age;
        this.gender = gender;
        this.about = about;
        this.category = category;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.address = address;
        this.mobile = mobile;
        this.pincode = pincode;
        this.availability = availability;
    }

    public Servant(String id, String name, String age, String gender, String cost, boolean availability, String imageUrl, String address, String mobile) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.cost = cost;
        this.address = address;
        this.mobile = mobile;
        this.imageUrl = imageUrl;
        this.availability = availability;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameOther() {
        return nameOther;
    }

    public void setNameOther(String nameOther) {
        this.nameOther = nameOther;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image) {
        this.imageUrl = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}
