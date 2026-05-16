package com.example.dictionary.service;

import com.example.dictionary.entity.AuthenticationVendor;
import com.example.dictionary.repository.AuthenticationVendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationVendorService {
    @Autowired
    private AuthenticationVendorRepository repository;

    public List<AuthenticationVendor> findAll() {
        return repository.findAll();
    }

    public Optional<AuthenticationVendor> findById(Long id) {
        return repository.findById(id);
    }

    public AuthenticationVendor save(AuthenticationVendor entity) {

        cleanupEntity(entity);
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private void cleanupEntity(AuthenticationVendor entity) {
        if (entity.getAuthenticationType() != null && (entity.getAuthenticationType().trim().isEmpty() || entity.getAuthenticationType().trim().equals("Select"))) {
            entity.setAuthenticationType(null);
        }
        if (entity.getAuthDetailsRouting() != null && (entity.getAuthDetailsRouting().trim().isEmpty() || entity.getAuthDetailsRouting().trim().equals("Select"))) {
            entity.setAuthDetailsRouting(null);
        }
        if (entity.getAuthorizationType() != null && (entity.getAuthorizationType().trim().isEmpty() || entity.getAuthorizationType().trim().equals("Select"))) {
            entity.setAuthorizationType(null);
        }
        if (entity.getVendorApp() != null && (entity.getVendorApp().trim().isEmpty() || entity.getVendorApp().trim().equals("Select"))) {
            entity.setVendorApp(null);
        }
        if (entity.getVendorName() != null && (entity.getVendorName().trim().isEmpty() || entity.getVendorName().trim().equals("Select"))) {
            entity.setVendorName(null);
        }
        if (entity.getAppDecommissioned() != null && (entity.getAppDecommissioned().trim().isEmpty() || entity.getAppDecommissioned().trim().equals("Select"))) {
            entity.setAppDecommissioned(null);
        }
        if (entity.getVendorTicketingUrl() != null && (entity.getVendorTicketingUrl().trim().isEmpty() || entity.getVendorTicketingUrl().trim().equals("Select"))) {
            entity.setVendorTicketingUrl(null);
        }
        if (entity.getVendorSupportExist() != null && (entity.getVendorSupportExist().trim().isEmpty() || entity.getVendorSupportExist().trim().equals("Select"))) {
            entity.setVendorSupportExist(null);
        }
        if (entity.getVendorContactDetails() != null && (entity.getVendorContactDetails().trim().isEmpty() || entity.getVendorContactDetails().trim().equals("Select"))) {
            entity.setVendorContactDetails(null);
        }
        if (entity.getAppHostedIn() != null && (entity.getAppHostedIn().trim().isEmpty() || entity.getAppHostedIn().trim().equals("Select"))) {
            entity.setAppHostedIn(null);
        }
    }
}
