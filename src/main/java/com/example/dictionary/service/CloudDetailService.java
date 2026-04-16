package com.example.dictionary.service;

import com.example.dictionary.entity.CloudDetail;
import com.example.dictionary.repository.CloudDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

@Service
public class CloudDetailService {
    @Autowired
    private CloudDetailRepository repository;

    public List<CloudDetail> findAll() {
        return repository.findAll();
    }

    public Optional<CloudDetail> findById(Long id) {
        return repository.findById(id);
    }

    public List<CloudDetail> findByEnvironment(String environment) {
        return repository.findByEnvironment(environment);
    }

    public CloudDetail save(CloudDetail entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }    public List<CloudDetail> processBulkGridData(Map<String, Object> gridData) {
        List<CloudDetail> savedRecords = new ArrayList<>();
        
        System.out.println("=== CLOUD DETAIL: Received GridData ===");
        System.out.println("GridData: " + gridData);
        
        // Process each section (non-prod, prod)
        for (Map.Entry<String, Object> entry : gridData.entrySet()) {
            String sectionId = entry.getKey();
            Object sectionData = entry.getValue();
            
            System.out.println("Processing section: " + sectionId + " with data: " + sectionData);
            
            // Determine environment from section ID
            String environment = null;
            if (sectionId.equals("non-prod")) {
                environment = "NON_PROD";
            } else if (sectionId.equals("prod")) {
                environment = "PROD";
            }
            
            // If sectionData is a list of rows
            if (sectionData instanceof List) {
                List<?> rows = (List<?>) sectionData;
                System.out.println("Found " + rows.size() + " rows in " + sectionId);
                for (Object rowObj : rows) {
                    if (rowObj instanceof Map) {
                        Map<?, ?> rowData = (Map<?, ?>) rowObj;
                        CloudDetail detail = new CloudDetail();
                        detail.setEnvironment(environment);
                        
                        // Map indexed columns to entity fields
                        // Field order from HTML: accountId, hostType, serviceName, lambdaNames, s3Bucket, sqsNames, iamUser, comments
                        detail.setAccountId(getValueAtIndex(rowData, 0));
                        detail.setHostType(getValueAtIndex(rowData, 1));
                        detail.setServiceName(getValueAtIndex(rowData, 2));
                        detail.setLambdaNames(getValueAtIndex(rowData, 3));
                        detail.setS3Bucket(getValueAtIndex(rowData, 4));
                        detail.setSqsNames(getValueAtIndex(rowData, 5));
                        detail.setIamUser(getValueAtIndex(rowData, 6));
                        detail.setComments(getValueAtIndex(rowData, 7));
                        
                        System.out.println("Detail: env=" + detail.getEnvironment() + ", accountId=" + detail.getAccountId());
                        
                        // Only save if at least one field has data
                        if (hasData(detail)) {
                            CloudDetail saved = repository.save(detail);
                            System.out.println("Saved: " + saved.getId());
                            savedRecords.add(saved);
                        }
                    }
                }
            }
        }
        
        System.out.println("=== CLOUD DETAIL: Total saved records: " + savedRecords.size() + " ===");
        return savedRecords;
    }

    private String getValueAtIndex(Map<?, ?> rowData, int index) {
        Object value = rowData.get("col_" + index);
        if (value != null && !value.toString().isEmpty()) {
            return value.toString();
        }
        return null;
    }

    private boolean hasData(CloudDetail detail) {
        return detail.getAccountId() != null || 
               detail.getHostType() != null || 
               detail.getServiceName() != null || 
               detail.getLambdaNames() != null || 
               detail.getS3Bucket() != null || 
               detail.getSqsNames() != null || 
               detail.getIamUser() != null || 
               detail.getComments() != null;
    }
}
