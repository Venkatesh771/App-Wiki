package com.example.dictionary.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class AuthenticationVendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_identity_id", nullable = true)
    @JsonBackReference
    private BasicIdentity basicIdentity;

    private String authenticationType;
    @Column(length = 2000)
    private String authDetailsRouting;
    private String authorizationType;
    private String vendorApp;
    private String vendorName;
    private String appDecommissioned;
    private String vendorTicketingUrl;    private String vendorSupportExist;
    private String vendorContactDetails;
    private String appHostedIn;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthDetailsRouting() {
        return authDetailsRouting;
    }

    public void setAuthDetailsRouting(String authDetailsRouting) {
        this.authDetailsRouting = authDetailsRouting;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getAppDecommissioned() {
        return appDecommissioned;
    }

    public void setAppDecommissioned(String appDecommissioned) {
        this.appDecommissioned = appDecommissioned;
    }

    public String getVendorTicketingUrl() {
        return vendorTicketingUrl;
    }

    public void setVendorTicketingUrl(String vendorTicketingUrl) {
        this.vendorTicketingUrl = vendorTicketingUrl;
    }

    public String getVendorSupportExist() {
        return vendorSupportExist;
    }

    public void setVendorSupportExist(String vendorSupportExist) {
        this.vendorSupportExist = vendorSupportExist;
    }

    public String getVendorContactDetails() {
        return vendorContactDetails;
    }

    public void setVendorContactDetails(String vendorContactDetails) {
        this.vendorContactDetails = vendorContactDetails;
    }    public String getAppHostedIn() {
        return appHostedIn;
    }    public void setAppHostedIn(String appHostedIn) {
        this.appHostedIn = appHostedIn;
    }

    public BasicIdentity getBasicIdentity() { return basicIdentity; }
    public void setBasicIdentity(BasicIdentity basicIdentity) { this.basicIdentity = basicIdentity; }
}
