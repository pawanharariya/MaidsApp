package com.anvay.partnerspanel.models;

public class NotificationItem {
    private String id;
    private String info;
    private int type;
    private String customerName;
    private String sex;
    private String address;

    //Type 0 for instantMaids and type 1 for 24X7 maids
    public NotificationItem(String id, String info, String name, String sex, String address, int type) {
        this.id = id;
        this.info = info;
        this.type = type;
        this.customerName = name;
        this.sex = sex;
        this.address = address;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getInfo() {
        return info;
    }

    public int getType() {
        return type;
    }

}
