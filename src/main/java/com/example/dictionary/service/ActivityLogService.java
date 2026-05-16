package com.example.dictionary.service;

import com.example.dictionary.entity.ActivityLog;
import com.example.dictionary.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository repository;

    public void recordAppAdd(String actorCwid, String actorName, String actorRole, String beatId, String appName) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_APP_ADD);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setActorRole(actorRole);
        log.setBeatId(beatId);
        log.setAppName(appName);
        repository.save(log);
    }

    public void recordAppEdit(String actorCwid, String actorName, String beatId, String appName,
                              String description, String oldValue, String newValue) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_APP_EDIT);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setBeatId(beatId);
        log.setAppName(appName);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        repository.save(log);
    }

    public void recordAppDelete(String actorCwid, String actorName, String beatId, String appName,
                                String description, String oldValue) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_APP_DELETE);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setBeatId(beatId);
        log.setAppName(appName);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue("(deleted)");
        repository.save(log);
    }

    public void recordUserEdit(String actorCwid, String actorName, String targetCwid, String targetUsername,
                               String description, String oldValue, String newValue) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_USER_EDIT);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setTargetCwid(targetCwid);
        log.setTargetUsername(targetUsername);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        repository.save(log);
    }

    public void recordUserDelete(String actorCwid, String actorName, String targetCwid, String targetUsername,
                                 String description, String oldValue) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_USER_DELETE);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setTargetCwid(targetCwid);
        log.setTargetUsername(targetUsername);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue("(deleted)");
        repository.save(log);
    }

    public void recordGroupAdd(String actorCwid, String actorName, String l15Name, String l2Name) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_GROUP_ADD);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setL15Name(l15Name);
        log.setL2Name(l2Name);
        repository.save(log);
    }

    public void recordGroupEdit(String actorCwid, String actorName,
                                String description, String oldValue, String newValue) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_GROUP_EDIT);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        repository.save(log);
    }

    public void recordGroupDelete(String actorCwid, String actorName,
                                  String description, String oldValue) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_GROUP_DELETE);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue("(deleted)");
        repository.save(log);
    }

    public List<ActivityLog> listAllEditsAndDeletes() {

        return repository.findByTypeInOrderByCreatedAtDesc(Arrays.asList(
                ActivityLog.TYPE_APP_EDIT,
                ActivityLog.TYPE_APP_DELETE,
                ActivityLog.TYPE_USER_EDIT,
                ActivityLog.TYPE_USER_DELETE,
                ActivityLog.TYPE_GROUP_EDIT,
                ActivityLog.TYPE_GROUP_DELETE
        )).stream()
          .filter(l -> l.getDescription() != null && !l.getDescription().isBlank())
          .toList();
    }

    public void recordUserAdd(String actorCwid, String actorName, String targetCwid, String targetUsername) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_USER_ADD);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setTargetCwid(targetCwid);
        log.setTargetUsername(targetUsername);
        repository.save(log);
    }

    public void recordUserLogin(String actorCwid, String actorName, String actorRole) {
        ActivityLog log = new ActivityLog();
        log.setType(ActivityLog.TYPE_USER_LOGIN);
        log.setActorCwid(actorCwid);
        log.setActorName(actorName);
        log.setActorRole(actorRole);
        repository.save(log);
    }

    public List<ActivityLog> listByType(String type) {
        return repository.findByTypeOrderByCreatedAtDesc(type);
    }
}
