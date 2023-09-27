package com.psh.maids.models;

import java.util.Map;

public class Review {

    String name;
    Double rating;
    String reviews;
    String date;
    String city;
    String servantId;
    String imageUrl;

    public Review() {
    }

    public Review(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.city = (String) map.get("city");
        this.date = (String) map.get("date");
        this.rating = (double) map.get("rating");
        this.reviews = (String) map.get("reviews");
        this.servantId = (String) map.get("servantId");
        this.imageUrl = (String) map.get("imageUrl");
    }

    public Review(String name, Double rating, String reviews, String date, String city, String servantId, String imageUrl) {
        this.name = name;
        this.rating = rating;
        this.reviews = reviews;
        this.date = date;
        this.city = city;
        this.imageUrl = imageUrl;
        this.servantId = servantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double mRating) {
        this.rating = mRating;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String mReviews) {
        this.reviews = mReviews;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String mDate) {
        this.date = mDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String mCity) {
        this.city = mCity;
    }

    public String getServantId() {
        return servantId;
    }

    public void setServantId(String mServantId) {
        this.servantId = mServantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
