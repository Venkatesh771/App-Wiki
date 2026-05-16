package com.example.dictionary.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cwid;

    @Column(nullable = false)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String authType;

    @Column(name = "filter_groups", columnDefinition = "CLOB", nullable = true)
    private String filterGroups;

    public User() {
    }

    public User(String cwid, String username, String email, String password, String role, boolean active) {
        this.cwid = cwid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
        this.authType = "LOCAL";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCwid() {
        return cwid;
    }

    public void setCwid(String cwid) {
        this.cwid = cwid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getFilterGroups() {
        return filterGroups;
    }

    public void setFilterGroups(String filterGroups) {
        this.filterGroups = filterGroups;
    }
}
