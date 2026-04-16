package com.example.dictionary.service;

import com.example.dictionary.entity.ApplicationServerDetail;
import com.example.dictionary.repository.ApplicationServerDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

@Service
public class ApplicationServerDetailService {
    @Autowired
    private ApplicationServerDetailRepository repository;

    public List<ApplicationServerDetail> findAll() {
        return repository.findAll();
    }

    public Optional<ApplicationServerDetail> findById(Long id) {
        return repository.findById(id);
    }

    public List<ApplicationServerDetail> findByEnvironment(String environment) {
        return repository.findByEnvironment(environment);
    }

    public ApplicationServerDetail save(ApplicationServerDetail entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }    public List<ApplicationServerDetail> processBulkGridData(Map<String, Object> gridData) {
        List<ApplicationServerDetail> savedRecords = new ArrayList<>();
        
        System.out.println("=== APP SERVER: Received GridData ===");
        System.out.println("GridData: " + gridData);
        
        // Process each section (dev-section, test-section, qa-section, prod-section)
        for (Map.Entry<String, Object> entry : gridData.entrySet()) {
            String sectionId = entry.getKey();
            Object sectionData = entry.getValue();
            
            System.out.println("Processing section: " + sectionId + " with data: " + sectionData);
            
            // Determine environment from section ID
            String environment = null;
            if (sectionId.equals("dev-section")) {
                environment = "DEV";
            } else if (sectionId.equals("test-section")) {
                environment = "TEST";
            } else if (sectionId.equals("qa-section")) {
                environment = "QA";
            } else if (sectionId.equals("prod-section")) {
                environment = "PROD";
            }
            
            // If sectionData is a list of rows
            if (sectionData instanceof List) {
                List<?> rows = (List<?>) sectionData;
                System.out.println("Found " + rows.size() + " rows in " + sectionId);
                for (Object rowObj : rows) {
                    if (rowObj instanceof Map) {
                        Map<?, ?> rowData = (Map<?, ?>) rowObj;
                        ApplicationServerDetail detail = new ApplicationServerDetail();
                        detail.setEnvironment(environment);
                        
                        // Map indexed columns to entity fields
                        // Field order from HTML: deployedServer, serverName, serverOsVersion, domain, cluster, serviceName, ipAddress
                        detail.setDeployedServer(getValueAtIndex(rowData, 0));
                        detail.setServerName(getValueAtIndex(rowData, 1));
                        detail.setServerOsVersion(getValueAtIndex(rowData, 2));
                        detail.setDomain(getValueAtIndex(rowData, 3));
                        detail.setCluster(getValueAtIndex(rowData, 4));
                        detail.setServiceName(getValueAtIndex(rowData, 5));
                        detail.setIpAddress(getValueAtIndex(rowData, 6));
                        
                        System.out.println("Detail: env=" + detail.getEnvironment() + ", serverName=" + detail.getServerName());
                        
                        // Only save if at least one field has data
                        if (hasData(detail)) {
                            ApplicationServerDetail saved = repository.save(detail);
                            System.out.println("Saved: " + saved.getId());
                            savedRecords.add(saved);
                        }
                    }
                }
            }
        }
        
        System.out.println("=== APP SERVER: Total saved records: " + savedRecords.size() + " ===");
        return savedRecords;
    }

    private String getValueAtIndex(Map<?, ?> rowData, int index) {
        Object value = rowData.get("col_" + index);
        if (value != null && !value.toString().isEmpty()) {
            return value.toString();
        }
        return null;
    }

    private boolean hasData(ApplicationServerDetail detail) {
        return detail.getDeployedServer() != null || 
               detail.getServerName() != null || 
               detail.getServerOsVersion() != null || 
               detail.getDomain() != null || 
               detail.getCluster() != null || 
               detail.getServiceName() != null || 
               detail.getIpAddress() != null;
    }
}
