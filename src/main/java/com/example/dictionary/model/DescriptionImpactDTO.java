package com.example.dictionary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DescriptionImpactDTO {    @JsonProperty("basicIdentityId")
    private Long basicIdentityId;
    @JsonProperty("applicationDescription")
    private String applicationDescription;
    @JsonProperty("businessImpactDescription")
    private String businessImpactDescription;
    @JsonProperty("financialImpact")
    private String financialImpact;
    @JsonProperty("userAccessReview")
    private String userAccessReview;

    public Long getBasicIdentityId() {
        return basicIdentityId;
    }

    public void setBasicIdentityId(Long basicIdentityId) {
        this.basicIdentityId = basicIdentityId;
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
    }

    public void setUserAccessReview(String userAccessReview) {
        this.userAccessReview = userAccessReview;
    }
}
