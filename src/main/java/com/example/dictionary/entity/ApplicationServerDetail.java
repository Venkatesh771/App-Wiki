package com.example.dictionary.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class ApplicationServerDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_identity_id", nullable = true)
    @JsonBackReference
    private BasicIdentity basicIdentity;

    private String environment; // DEV, TEST, QA, PROD
    private String serverName;
    private String serverOsVersion;
    private String deployedServer;
    private String domain;
    @Column(name = "\"CLUSTER\"")
    private String cluster;
    private String serviceName;
    private String ipAddress;
    private Boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }
    public String getServerOsVersion() { return serverOsVersion; }
    public void setServerOsVersion(String serverOsVersion) { this.serverOsVersion = serverOsVersion; }
    public String getDeployedServer() { return deployedServer; }
    public void setDeployedServer(String deployedServer) { this.deployedServer = deployedServer; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getCluster() { return cluster; }
    public void setCluster(String cluster) { this.cluster = cluster; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public BasicIdentity getBasicIdentity() { return basicIdentity; }
    public void setBasicIdentity(BasicIdentity basicIdentity) { this.basicIdentity = basicIdentity; }
}
