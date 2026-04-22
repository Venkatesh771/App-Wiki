package com.example.dictionary.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class CloudDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_identity_id", nullable = true)
    @JsonBackReference
    private BasicIdentity basicIdentity;

    private String environment; // NON_PROD or PROD
    private String accountId;
    private String hostType;
    @Column(length = 2000)
    private String serviceName;
    @Column(length = 2000)
    private String lambdaNames;
    @Column(length = 2000)
    private String s3Bucket;
    @Column(length = 2000)
    private String sqsNames;
    private String iamUser;
    @Column(length = 2000)
    private String comments;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getHostType() { return hostType; }
    public void setHostType(String hostType) { this.hostType = hostType; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getLambdaNames() { return lambdaNames; }
    public void setLambdaNames(String lambdaNames) { this.lambdaNames = lambdaNames; }
    public String getS3Bucket() { return s3Bucket; }
    public void setS3Bucket(String s3Bucket) { this.s3Bucket = s3Bucket; }
    public String getSqsNames() { return sqsNames; }
    public void setSqsNames(String sqsNames) { this.sqsNames = sqsNames; }
    public String getIamUser() { return iamUser; }
    public void setIamUser(String iamUser) { this.iamUser = iamUser; }    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public BasicIdentity getBasicIdentity() { return basicIdentity; }
    public void setBasicIdentity(BasicIdentity basicIdentity) { this.basicIdentity = basicIdentity; }
}
