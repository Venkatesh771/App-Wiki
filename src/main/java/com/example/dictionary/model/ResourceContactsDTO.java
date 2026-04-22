package com.example.dictionary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceContactsDTO {    @JsonProperty("basicIdentityId")
    private Long basicIdentityId;
    @JsonProperty("onshorePrimary")
    private String onshorePrimary;
    @JsonProperty("onshoreSecondary")
    private String onshoreSecondary;
    @JsonProperty("onshoreTertiary")
    private String onshoreTertiary;
    @JsonProperty("offshorePrimary")
    private String offshorePrimary;
    @JsonProperty("offshoreSecondary")
    private String offshoreSecondary;
    @JsonProperty("offshoreTertiary")
    private String offshoreTertiary;

    public Long getBasicIdentityId() {
        return basicIdentityId;
    }

    public void setBasicIdentityId(Long basicIdentityId) {
        this.basicIdentityId = basicIdentityId;
    }    public String getOnshorePrimary() {
        return onshorePrimary;
    }

    public void setOnshorePrimary(String onshorePrimary) {
        this.onshorePrimary = onshorePrimary;
    }

    public String getOnshoreSecondary() {
        return onshoreSecondary;
    }

    public void setOnshoreSecondary(String onshoreSecondary) {
        this.onshoreSecondary = onshoreSecondary;
    }

    public String getOnshoreTertiary() {
        return onshoreTertiary;
    }

    public void setOnshoreTertiary(String onshoreTertiary) {
        this.onshoreTertiary = onshoreTertiary;
    }

    public String getOffshorePrimary() {
        return offshorePrimary;
    }

    public void setOffshorePrimary(String offshorePrimary) {
        this.offshorePrimary = offshorePrimary;
    }

    public String getOffshoreSecondary() {
        return offshoreSecondary;
    }

    public void setOffshoreSecondary(String offshoreSecondary) {
        this.offshoreSecondary = offshoreSecondary;
    }

    public String getOffshoreTertiary() {
        return offshoreTertiary;
    }

    public void setOffshoreTertiary(String offshoreTertiary) {
        this.offshoreTertiary = offshoreTertiary;
    }
}
