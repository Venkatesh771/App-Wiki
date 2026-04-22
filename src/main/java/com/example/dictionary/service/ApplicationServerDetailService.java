package com.example.dictionary.service;

import com.example.dictionary.entity.ApplicationServerDetail;
import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.repository.ApplicationServerDetailRepository;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

@Service
public class ApplicationServerDetailService {
    @Autowired
    private ApplicationServerDetailRepository repository;

    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

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
        return processBulkGridData(gridData, null, null);
    }

    @Transactional
    public List<ApplicationServerDetail> processBulkGridData(Map<String, Object> gridData, Long basicIdentityId) {
        return processBulkGridData(gridData, basicIdentityId, null);
    }

    @Transactional
    public List<ApplicationServerDetail> processBulkGridData(Map<String, Object> gridData, Long basicIdentityId, String beatId) {
        List<ApplicationServerDetail> savedRecords = new ArrayList<>();
        
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
        
        System.out.println("=== APP SERVER: Received GridData ===");
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
                            ApplicationServerDetail detail = new ApplicationServerDetail();
                            detail.setEnvironment(environment);
                            detail.setBasicIdentity(basicIdentity);
                            
                            // Map NAMED fields from frontend to entity fields
                            // Frontend sends: serverName, serverOsVersion, deployedServer, domain, cluster, serviceName, ipAddress
                            detail.setDeployedServer(getNamedValue(rowData, "deployedServer"));
                            detail.setServerName(getNamedValue(rowData, "serverName"));
                            detail.setServerOsVersion(getNamedValue(rowData, "serverOsVersion"));
                            detail.setDomain(getNamedValue(rowData, "domain"));
                            detail.setCluster(getNamedValue(rowData, "cluster"));
                            detail.setServiceName(getNamedValue(rowData, "serviceName"));
                            detail.setIpAddress(getNamedValue(rowData, "ipAddress"));
                            
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
        } catch (Exception e) {
            System.err.println("ERROR in ApplicationServerDetailService.processBulkGridData: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== APP SERVER: Total saved records: " + savedRecords.size() + " ===");
        return savedRecords;
    }    private String getNamedValue(Map<?, ?> rowData, String fieldName) {
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

    private String getValueAtIndex(Map<?, ?> rowData, int index) {
        Object value = rowData.get("col_" + index);
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
