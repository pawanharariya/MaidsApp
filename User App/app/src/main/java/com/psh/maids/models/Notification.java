package com.psh.maids.models;

public class Notification {
    String id;//ID is required to delete that document from firebase once requirement is fulfilled
    String info;
    String confirmedId;
    String requirementType;
    String amount;

    public Notification(String id, String info, String confirmedId, String requirementType, String amount) {
        this.id = id;
        this.info = info;
        this.confirmedId = confirmedId;
        this.requirementType = requirementType;
        this.amount = amount;
    }

    public String getId() {
        return this.id;
    }

    public String getInfo() {
        return this.info;
    }

    public String getConfirmedId() {
        return this.confirmedId;
    }

    public String getRequirementType() {
        return this.requirementType;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
