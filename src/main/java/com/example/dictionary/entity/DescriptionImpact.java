package com.example.dictionary.entity;

import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class DescriptionImpact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String applicationDescription;    @Column(length = 2000)    private String businessImpactDescription;
    private String financialImpact;
    private String userAccessReview;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public String getBusinessImpactDescription() {
        return businessImpactDescription;
    }

    public void setBusinessImpactDescription(String businessImpactDescription) {
        this.businessImpactDescription = businessImpactDescription;
    }

    public String getFinancialImpact() {
        return financialImpact;
    }

    public void setFinancialImpact(String financialImpact) {
        this.financialImpact = financialImpact;
    }

    public String getUserAccessReview() {
        return userAccessReview;
    }    public void setUserAccessReview(String userAccessReview) {
        this.userAccessReview = userAccessReview;
    }
}
