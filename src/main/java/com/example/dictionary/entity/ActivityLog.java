package com.example.dictionary.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs", indexes = {
        @Index(name = "idx_activity_logs_type_created", columnList = "type, created_at")
})
public class ActivityLog {

    public static final String TYPE_APP_ADD = "APP_ADD";
    public static final String TYPE_APP_EDIT = "APP_EDIT";
    public static final String TYPE_APP_DELETE = "APP_DELETE";
    public static final String TYPE_USER_ADD = "USER_ADD";
    public static final String TYPE_USER_EDIT = "USER_EDIT";
    public static final String TYPE_USER_DELETE = "USER_DELETE";
    public static final String TYPE_GROUP_ADD = "GROUP_ADD";
    public static final String TYPE_GROUP_EDIT = "GROUP_EDIT";
    public static final String TYPE_GROUP_DELETE = "GROUP_DELETE";
    public static final String TYPE_USER_LOGIN = "USER_LOGIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String type;

    @Column(name = "actor_cwid", length = 64)
    private String actorCwid;

    @Column(name = "actor_name", length = 200)
    private String actorName;

    @Column(name = "beat_id", length = 128)
    private String beatId;

    @Column(name = "app_name", length = 256)
    private String appName;

    @Column(name = "target_cwid", length = 64)
    private String targetCwid;

    @Column(name = "target_username", length = 200)
    private String targetUsername;

    @Column(name = "actor_role", length = 50)
    private String actorRole;

    @Column(name = "l15_name", length = 200)
    private String l15Name;

    @Column(name = "l2_name", length = 200)
    private String l2Name;

    @Column(name = "description", columnDefinition = "CLOB")
    private String description;

    @Column(name = "old_value", columnDefinition = "CLOB")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "CLOB")
    private String newValue;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ActivityLog() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getActorCwid() { return actorCwid; }
    public void setActorCwid(String actorCwid) { this.actorCwid = actorCwid; }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public String getBeatId() { return beatId; }
    public void setBeatId(String beatId) { this.beatId = beatId; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getTargetCwid() { return targetCwid; }
    public void setTargetCwid(String targetCwid) { this.targetCwid = targetCwid; }

    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public String getL15Name() { return l15Name; }
    public void setL15Name(String l15Name) { this.l15Name = l15Name; }

    public String getL2Name() { return l2Name; }
    public void setL2Name(String l2Name) { this.l2Name = l2Name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
