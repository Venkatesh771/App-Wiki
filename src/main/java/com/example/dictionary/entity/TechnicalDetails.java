package com.example.dictionary.entity;

import jakarta.persistence.*;

@Entity
public class TechnicalDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000)
    private String appUrlsDevTest;
    private String githubRepos;
    private String lbVipDetails;
    private String externalInternal;
    @Column(length = 2000)
    private String upstreamSystem;
    @Column(length = 2000)
    private String akanaMulesoft;
    @Column(length = 2000)
    private String kafkaTopic;
    @Column(length = 2000)
    private String azureClientIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppUrlsDevTest() { return appUrlsDevTest; }
    public void setAppUrlsDevTest(String appUrlsDevTest) { this.appUrlsDevTest = appUrlsDevTest; }
    public String getGithubRepos() { return githubRepos; }
    public void setGithubRepos(String githubRepos) { this.githubRepos = githubRepos; }
    public String getLbVipDetails() { return lbVipDetails; }
    public void setLbVipDetails(String lbVipDetails) { this.lbVipDetails = lbVipDetails; }
    public String getExternalInternal() { return externalInternal; }
    public void setExternalInternal(String externalInternal) { this.externalInternal = externalInternal; }
    public String getUpstreamSystem() { return upstreamSystem; }
    public void setUpstreamSystem(String upstreamSystem) { this.upstreamSystem = upstreamSystem; }
    public String getAkanaMulesoft() { return akanaMulesoft; }
    public void setAkanaMulesoft(String akanaMulesoft) { this.akanaMulesoft = akanaMulesoft; }
    public String getKafkaTopic() { return kafkaTopic; }
    public void setKafkaTopic(String kafkaTopic) { this.kafkaTopic = kafkaTopic; }
    public String getAzureClientIds() { return azureClientIds; }
    public void setAzureClientIds(String azureClientIds) { this.azureClientIds = azureClientIds; }
}
