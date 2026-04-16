package com.example.dictionary.entity;

import jakarta.persistence.*;

@Entity
public class BasicIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String beatId;
    private String applicationName;
    private String gxp;
    private String squad;
    private String businessOwner;
    private String subDomain;
    private String appRegion;
    private String serviceVariant;    private String typeCategory;
    private String systemOwner;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeatId() {
        return beatId;
    }

    public void setBeatId(String beatId) {
        this.beatId = beatId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getGxp() {
        return gxp;
    }

    public void setGxp(String gxp) {
        this.gxp = gxp;
    }

    public String getSquad() {
        return squad;
    }

    public void setSquad(String squad) {
        this.squad = squad;
    }

    public String getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getAppRegion() {
        return appRegion;
    }

    public void setAppRegion(String appRegion) {
        this.appRegion = appRegion;
    }

    public String getServiceVariant() {
        return serviceVariant;
    }

    public void setServiceVariant(String serviceVariant) {
        this.serviceVariant = serviceVariant;
    }

    public String getTypeCategory() {
        return typeCategory;
    }

    public void setTypeCategory(String typeCategory) {
        this.typeCategory = typeCategory;
    }

    public String getSystemOwner() {
        return systemOwner;
    }    public void setSystemOwner(String systemOwner) {
        this.systemOwner = systemOwner;
    }
}