package com.example.dictionary.service;

import com.example.dictionary.entity.DatabaseServerDetail;
import com.example.dictionary.repository.DatabaseServerDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

@Service
public class DatabaseServerDetailService {
    @Autowired
    private DatabaseServerDetailRepository repository;

    public List<DatabaseServerDetail> findAll() {
        return repository.findAll();
    }

    public Optional<DatabaseServerDetail> findById(Long id) {
        return repository.findById(id);
    }

    public List<DatabaseServerDetail> findByEnvironment(String environment) {
        return repository.findByEnvironment(environment);
    }

    public DatabaseServerDetail save(DatabaseServerDetail entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<DatabaseServerDetail> processBulkGridData(Map<String, Object> gridData) {
        List<DatabaseServerDetail> savedRecords = new ArrayList<>();
        
        // Process each section (dev-section, test-section, qa-section, prod-section)
        for (Map.Entry<String, Object> entry : gridData.entrySet()) {
            String sectionId = entry.getKey();
            Object sectionData = entry.getValue();
            
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
                for (Object rowObj : rows) {
                    if (rowObj instanceof Map) {
                        Map<?, ?> rowData = (Map<?, ?>) rowObj;
                        DatabaseServerDetail detail = new DatabaseServerDetail();
                        detail.setEnvironment(environment);
                        
                        // Map indexed columns to entity fields
                        // Field order from HTML: databaseType, databaseVersion, databaseHostingType, databaseName, accountName, hostName, serviceName, port, accountId, ip
                        detail.setDatabaseType(getValueAtIndex(rowData, 0));
                        detail.setDatabaseVersion(getValueAtIndex(rowData, 1));
                        detail.setDatabaseHostingType(getValueAtIndex(rowData, 2));
                        detail.setDatabaseName(getValueAtIndex(rowData, 3));
                        detail.setAccountName(getValueAtIndex(rowData, 4));
                        detail.setHostName(getValueAtIndex(rowData, 5));
                        detail.setServiceName(getValueAtIndex(rowData, 6));
                        detail.setPort(getValueAtIndex(rowData, 7));
                        detail.setAccountId(getValueAtIndex(rowData, 8));
                        detail.setIp(getValueAtIndex(rowData, 9));
                        
                        // Only save if at least one field has data
                        if (hasData(detail)) {
                            savedRecords.add(repository.save(detail));
                        }
                    }
                }
            }
        }
        
        return savedRecords;
    }

    private String getValueAtIndex(Map<?, ?> rowData, int index) {
        Object value = rowData.get("col_" + index);
        if (value != null && !value.toString().isEmpty()) {
            return value.toString();
        }
        return null;
    }

    private boolean hasData(DatabaseServerDetail detail) {
        return detail.getDatabaseType() != null || 
               detail.getDatabaseVersion() != null || 
               detail.getDatabaseHostingType() != null || 
               detail.getDatabaseName() != null || 
               detail.getAccountName() != null || 
               detail.getHostName() != null || 
               detail.getServiceName() != null || 
               detail.getPort() != null || 
               detail.getAccountId() != null || 
               detail.getIp() != null;
    }
}
