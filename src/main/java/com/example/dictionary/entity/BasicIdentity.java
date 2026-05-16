package com.example.dictionary.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "uc_basic_identity_beat_id", columnNames = {"beat_id"}),
    @UniqueConstraint(name = "uc_basic_identity_app_name", columnNames = {"application_name"})
})
public class BasicIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beat_id")
    private String beatId;

    @Column(name = "application_name")
    private String applicationName;
    private String gxp;
    private String squad;
    private String businessOwner;
    private String subDomain;
    private String appRegion;
    private String serviceVariant;
    private String typeCategory;
    private String systemOwner;
    private String assignmentGroup;

    private Boolean active = true;

    @OneToMany(mappedBy = "basicIdentity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ApplicationServerDetail> applicationServerDetails;

    @OneToMany(mappedBy = "basicIdentity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CloudDetail> cloudDetails;

    @OneToMany(mappedBy = "basicIdentity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DatabaseServerDetail> databaseServerDetails;

    @OneToMany(mappedBy = "basicIdentity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AuthenticationVendor> authenticationVendors;

    @OneToMany(mappedBy = "basicIdentity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TechnicalDetails> technicalDetails;

    @OneToMany(mappedBy = "basicIdentity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DescriptionImpact> descriptionImpacts;

    @OneToMany(mappedBy = "basicIdentity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ResourceContacts> resourceContacts;

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
    }    public String getSystemOwner() {
        return systemOwner;
    }

    public void setSystemOwner(String systemOwner) {
        this.systemOwner = systemOwner;
    }

    public String getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(String assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ApplicationServerDetail> getApplicationServerDetails() {
        return applicationServerDetails;
    }

    public void setApplicationServerDetails(List<ApplicationServerDetail> applicationServerDetails) {
        this.applicationServerDetails = applicationServerDetails;
    }

    public List<CloudDetail> getCloudDetails() {
        return cloudDetails;
    }

    public void setCloudDetails(List<CloudDetail> cloudDetails) {
        this.cloudDetails = cloudDetails;
    }

    public List<DatabaseServerDetail> getDatabaseServerDetails() {
        return databaseServerDetails;
    }

    public void setDatabaseServerDetails(List<DatabaseServerDetail> databaseServerDetails) {
        this.databaseServerDetails = databaseServerDetails;
    }

    public List<AuthenticationVendor> getAuthenticationVendors() {
        return authenticationVendors;
    }

    public void setAuthenticationVendors(List<AuthenticationVendor> authenticationVendors) {
        this.authenticationVendors = authenticationVendors;
    }

    public List<TechnicalDetails> getTechnicalDetails() {
        return technicalDetails;
    }

    public void setTechnicalDetails(List<TechnicalDetails> technicalDetails) {
        this.technicalDetails = technicalDetails;
    }

    public List<DescriptionImpact> getDescriptionImpacts() {
        return descriptionImpacts;
    }

    public void setDescriptionImpacts(List<DescriptionImpact> descriptionImpacts) {
        this.descriptionImpacts = descriptionImpacts;
    }

    public List<ResourceContacts> getResourceContacts() {
        return resourceContacts;
    }

    public void setResourceContacts(List<ResourceContacts> resourceContacts) {
        this.resourceContacts = resourceContacts;
    }
}
