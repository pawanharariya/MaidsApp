package com.psh.maids.models;

public class User {
    String name;
    String mobile;
    String address;
    Boolean activeStatus;
    String firebaseId;

    public User() {

    }

    public User(String firebaseId, String name, String contact, String address, Boolean activeStatus) {
        this.activeStatus = activeStatus;
        this.mobile = contact;
        this.name = name;
        this.address = address;
        this.firebaseId = firebaseId;
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

    public void setMobile(String contact) {
        this.mobile = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
