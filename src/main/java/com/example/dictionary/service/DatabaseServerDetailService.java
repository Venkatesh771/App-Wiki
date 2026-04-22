package com.example.dictionary.service;

import com.example.dictionary.entity.DatabaseServerDetail;
import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.repository.DatabaseServerDetailRepository;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

@Service
public class DatabaseServerDetailService {
    @Autowired
    private DatabaseServerDetailRepository repository;

    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

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
        return processBulkGridData(gridData, null, null);
    }

    @Transactional
    public List<DatabaseServerDetail> processBulkGridData(Map<String, Object> gridData, Long basicIdentityId) {
        return processBulkGridData(gridData, basicIdentityId, null);
    }

    @Transactional
    public List<DatabaseServerDetail> processBulkGridData(Map<String, Object> gridData, Long basicIdentityId, String beatId) {
        List<DatabaseServerDetail> savedRecords = new ArrayList<>();
        
        // Fetch BasicIdentity if provided
        BasicIdentity basicIdentity = null;
        if (basicIdentityId != null) {
            basicIdentity = basicIdentityRepository.findById(basicIdentityId).orElse(null);
        }
        
        // Fallback: Find by beatId if basicIdentityId is null
        if (basicIdentity == null && beatId != null && !beatId.isEmpty()) {
            basicIdentity = basicIdentityRepository.findByBeatId(beatId).orElse(null);
            if (basicIdentity != null) {
                basicIdentityId = basicIdentity.getId();
                System.out.println("✅ Found BasicIdentity by beatId: " + beatId + " -> ID: " + basicIdentityId);
            }
        }
        
        System.out.println("=== DATABASE SERVER: Received GridData ===");
        System.out.println("GridData: " + gridData);
        System.out.println("BasicIdentityId: " + basicIdentityId);
        System.out.println("BeatId: " + beatId);
        
        try {
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
                            DatabaseServerDetail detail = new DatabaseServerDetail();
                            detail.setEnvironment(environment);
                            detail.setBasicIdentity(basicIdentity);
                            
                            // Map NAMED fields from frontend to entity fields
                            detail.setDatabaseType(getNamedValue(rowData, "databaseType"));
                            detail.setDatabaseVersion(getNamedValue(rowData, "databaseVersion"));
                            detail.setDatabaseHostingType(getNamedValue(rowData, "databaseHostingType"));
                            detail.setDatabaseName(getNamedValue(rowData, "databaseName"));
                            detail.setAccountName(getNamedValue(rowData, "accountName"));
                            detail.setHostName(getNamedValue(rowData, "hostName"));
                            detail.setServiceName(getNamedValue(rowData, "serviceName"));
                            detail.setPort(getNamedValue(rowData, "port"));
                            detail.setAccountId(getNamedValue(rowData, "accountId"));
                            detail.setIp(getNamedValue(rowData, "ip"));
                            
                            System.out.println("Detail: env=" + detail.getEnvironment() + ", dbType=" + detail.getDatabaseType());
                            
                            // Only save if at least one field has data
                            if (hasData(detail)) {
                                DatabaseServerDetail saved = repository.save(detail);
                                System.out.println("Saved: " + saved.getId());
                                savedRecords.add(saved);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR in DatabaseServerDetailService.processBulkGridData: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== DATABASE SERVER: Total saved records: " + savedRecords.size() + " ===");
        return savedRecords;
    }

    private String getNamedValue(Map<?, ?> rowData, String fieldName) {
        Object value = rowData.get(fieldName);
        if (value != null) {
            String strValue = value.toString().trim();
            // Filter out placeholder/empty values
            if (strValue.isEmpty() || strValue.equals("Select") || strValue.equals("--") || strValue.equals("N/A")) {
                return null;
            }
            return strValue;
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
