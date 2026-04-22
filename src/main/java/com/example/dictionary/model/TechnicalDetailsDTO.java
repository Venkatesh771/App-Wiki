package com.example.dictionary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TechnicalDetailsDTO {    @JsonProperty("basicIdentityId")
    private Long basicIdentityId;
    @JsonProperty("externalInternal")
    private String externalInternal;
    @JsonProperty("githubRepos")
    private String githubRepos;
    @JsonProperty("kafkaTopic")
    private String kafkaTopic;
    @JsonProperty("akanaMusoft")
    private String akanaMusoft;
    @JsonProperty("appUrlsDevTest")
    private String appUrlsDevTest;
    @JsonProperty("azureClientIds")
    private String azureClientIds;
    @JsonProperty("lbVipDetails")
    private String lbVipDetails;
    @JsonProperty("upstreamSystem")
    private String upstreamSystem;

    public Long getBasicIdentityId() {
        return basicIdentityId;
    }

    public void setBasicIdentityId(Long basicIdentityId) {
        this.basicIdentityId = basicIdentityId;
    }

    public String getExternalInternal() {
        return externalInternal;
    }

    public void setExternalInternal(String externalInternal) {
        this.externalInternal = externalInternal;
    }

    public String getGithubRepos() {
        return githubRepos;
    }

    public void setGithubRepos(String githubRepos) {
        this.githubRepos = githubRepos;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getAkanaMusoft() {
        return akanaMusoft;
    }

    public void setAkanaMusoft(String akanaMusoft) {
        this.akanaMusoft = akanaMusoft;
    }

    public String getAppUrlsDevTest() {
        return appUrlsDevTest;
    }

    public void setAppUrlsDevTest(String appUrlsDevTest) {
        this.appUrlsDevTest = appUrlsDevTest;
    }

    public String getAzureClientIds() {
        return azureClientIds;
    }

    public void setAzureClientIds(String azureClientIds) {
        this.azureClientIds = azureClientIds;
    }

    public String getLbVipDetails() {
        return lbVipDetails;
    }

    public void setLbVipDetails(String lbVipDetails) {
        this.lbVipDetails = lbVipDetails;
    }

    public String getUpstreamSystem() {
        return upstreamSystem;
    }

    public void setUpstreamSystem(String upstreamSystem) {
        this.upstreamSystem = upstreamSystem;
    }
}
