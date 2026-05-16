package com.example.dictionary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
public class IdentityCounterSync {

    private static final Logger log = LoggerFactory.getLogger(IdentityCounterSync.class);

    private static final String[] TABLES = {
            "activity_logs",
            "app_groups",
            "users",
            "basic_identity"
    };

    @PersistenceContext
    private EntityManager em;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void resyncOnStartup() {
        for (String table : TABLES) {
            try {
                Object max = em.createNativeQuery("SELECT COALESCE(MAX(id), 0) FROM " + table).getSingleResult();
                long next = ((Number) max).longValue() + 1;
                em.createNativeQuery("ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH " + next).executeUpdate();
            } catch (Exception e) {
                log.warn("Identity resync skipped for table {}: {}", table, e.getMessage());
            }
        }
    }
}
