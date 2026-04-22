package com.example.dictionary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationVendorDTO {    @JsonProperty("basicIdentityId")
    private Long basicIdentityId;
    @JsonProperty("vendorName")
    private String vendorName;
    @JsonProperty("authenticationType")
    private String authenticationType;
    @JsonProperty("authorizationType")
    private String authorizationType;
    @JsonProperty("vendorApp")
    private String vendorApp;
    @JsonProperty("vendorContactDetails")
    private String vendorContactDetails;
    @JsonProperty("vendorSupportExist")
    private String vendorSupportExist;
    @JsonProperty("vendorTicketingUrl")
    private String vendorTicketingUrl;
    @JsonProperty("appDecommissioned")
    private String appDecommissioned;
    @JsonProperty("appHostedIn")
    private String appHostedIn;
    @JsonProperty("authDetailsRouting")
    private String authDetailsRouting;

    public Long getBasicIdentityId() {
        return basicIdentityId;
    }

    public void setBasicIdentityId(Long basicIdentityId) {
        this.basicIdentityId = basicIdentityId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthorizationType() {
        return authorizationType;
    }

    public void setAuthorizationType(String authorizationType) {
        this.authorizationType = authorizationType;
    }

    public String getVendorApp() {
        return vendorApp;
    }

    public void setVendorApp(String vendorApp) {
        this.vendorApp = vendorApp;
    }

    public String getVendorContactDetails() {
        return vendorContactDetails;
    }

    public void setVendorContactDetails(String vendorContactDetails) {
        this.vendorContactDetails = vendorContactDetails;
    }

    public String getVendorSupportExist() {
        return vendorSupportExist;
    }

    public void setVendorSupportExist(String vendorSupportExist) {
        this.vendorSupportExist = vendorSupportExist;
    }

    public String getVendorTicketingUrl() {
        return vendorTicketingUrl;
    }

    public void setVendorTicketingUrl(String vendorTicketingUrl) {
        this.vendorTicketingUrl = vendorTicketingUrl;
    }

    public String getAppDecommissioned() {
        return appDecommissioned;
    }

    public void setAppDecommissioned(String appDecommissioned) {
        this.appDecommissioned = appDecommissioned;
    }

    public String getAppHostedIn() {
        return appHostedIn;
    }

    public void setAppHostedIn(String appHostedIn) {
        this.appHostedIn = appHostedIn;
    }

    public String getAuthDetailsRouting() {
        return authDetailsRouting;
    }

    public void setAuthDetailsRouting(String authDetailsRouting) {
        this.authDetailsRouting = authDetailsRouting;
    }
}
