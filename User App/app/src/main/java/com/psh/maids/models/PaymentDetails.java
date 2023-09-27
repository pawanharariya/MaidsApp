package com.psh.maids.models;

public class PaymentDetails {
    String customerId;
    String paymentId;
    String amount;
    String info;
    String servantId;

    public PaymentDetails() {
    }

    public PaymentDetails(String customerId, String servantId, String paymentId, String amount, String info) {
        this.customerId = customerId;
        this.servantId = servantId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.info = info;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getServantId() {
        return servantId;
    }

    public void setServantId(String servantId) {
        this.servantId = servantId;
    }

}
