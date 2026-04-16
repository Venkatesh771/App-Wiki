package com.example.dictionary.entity;

import jakarta.persistence.*;

@Entity
public class DatabaseServerDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String environment; // DEV, TEST, QA, PROD
    private String databaseType;
    private String databaseVersion;
    private String databaseHostingType;
    private String databaseName;
    private String accountName;
    private String hostName;
    private String serviceName;
    private String port;
    private String accountId;
    private String ip;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public String getDatabaseType() { return databaseType; }
    public void setDatabaseType(String databaseType) { this.databaseType = databaseType; }
    public String getDatabaseVersion() { return databaseVersion; }
    public void setDatabaseVersion(String databaseVersion) { this.databaseVersion = databaseVersion; }
    public String getDatabaseHostingType() { return databaseHostingType; }
    public void setDatabaseHostingType(String databaseHostingType) { this.databaseHostingType = databaseHostingType; }
    public String getDatabaseName() { return databaseName; }
    public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}
