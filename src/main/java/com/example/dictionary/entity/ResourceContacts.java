package com.example.dictionary.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class ResourceContacts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_identity_id", nullable = true)
    @JsonBackReference
    private BasicIdentity basicIdentity;

    private String offshorePrimary;
    private String offshoreSecondary;
    private String offshoreTertiary;
    private String onshorePrimary;
    private String onshoreSecondary;
    private String onshoreTertiary;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOffshorePrimary() { return offshorePrimary; }
    public void setOffshorePrimary(String offshorePrimary) { this.offshorePrimary = offshorePrimary; }
    public String getOffshoreSecondary() { return offshoreSecondary; }
    public void setOffshoreSecondary(String offshoreSecondary) { this.offshoreSecondary = offshoreSecondary; }
    public String getOffshoreTertiary() { return offshoreTertiary; }
    public void setOffshoreTertiary(String offshoreTertiary) { this.offshoreTertiary = offshoreTertiary; }
    public String getOnshorePrimary() { return onshorePrimary; }
    public void setOnshorePrimary(String onshorePrimary) { this.onshorePrimary = onshorePrimary; }
    public String getOnshoreSecondary() { return onshoreSecondary; }
    public void setOnshoreSecondary(String onshoreSecondary) { this.onshoreSecondary = onshoreSecondary; }    public String getOnshoreTertiary() { return onshoreTertiary; }
    public void setOnshoreTertiary(String onshoreTertiary) { this.onshoreTertiary = onshoreTertiary; }

    public BasicIdentity getBasicIdentity() { return basicIdentity; }
    public void setBasicIdentity(BasicIdentity basicIdentity) { this.basicIdentity = basicIdentity; }
}
