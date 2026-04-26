package com.example.dictionary.service;

import com.example.dictionary.entity.CloudDetail;
import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.repository.CloudDetailRepository;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

@Service
public class CloudDetailService {

    private static final Logger log = LoggerFactory.getLogger(CloudDetailService.class);

    @Autowired
    private CloudDetailRepository repository;

    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

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

    public CloudDetail save(CloudDetail entity, Long basicIdentityId) {
        if (basicIdentityId != null) {
            basicIdentityRepository.findById(basicIdentityId)
                    .ifPresent(entity::setBasicIdentity);
        }
        return repository.save(entity);
    }    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public boolean deactivate(Long id) {
        return repository.findById(id).map(e -> {
            e.setActive(false);
            repository.save(e);
            return true;
        }).orElse(false);
    }

    public List<CloudDetail> processBulkGridData(Map<String, Object> gridData) {
        return processBulkGridData(gridData, null, null);
    }

    public List<CloudDetail> processBulkGridData(Map<String, Object> gridData, Long basicIdentityId) {
        return processBulkGridData(gridData, basicIdentityId, null);
    }

    @Transactional
    public List<CloudDetail> processBulkGridData(Map<String, Object> gridData, Long basicIdentityId, String beatId) {
        List<CloudDetail> savedRecords = new ArrayList<>();
        
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
                log.info("Found BasicIdentity by beatId: {} -> ID: {}", beatId, basicIdentityId);
            }
        }

        log.info("=== CLOUD DETAIL: Received GridData ===");
        log.debug("GridData: {}", gridData);
        log.debug("BasicIdentityId: {}", basicIdentityId);
        log.debug("BeatId: {}", beatId);
        
        try {
            // Process each section (non-prod, prod)
            for (Map.Entry<String, Object> entry : gridData.entrySet()) {
                String sectionId = entry.getKey();
                Object sectionData = entry.getValue();
                
                log.debug("Processing section: {} with data: {}", sectionId, sectionData);
                
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
                    log.debug("Found {} rows in {}", rows.size(), sectionId);                    for (Object rowObj : rows) {
                        if (rowObj instanceof Map) {
                            Map<?, ?> rowData = (Map<?, ?>) rowObj;
                            CloudDetail detail = new CloudDetail();
                            detail.setEnvironment(environment);
                            detail.setBasicIdentity(basicIdentity);
                            
                            // Map NAMED fields from frontend to entity fields
                            // Frontend sends: accountId, hostType, serviceName, lambdaNames, s3Bucket, sqsNames, iamUser, comments
                            detail.setAccountId(getNamedValue(rowData, "accountId"));
                            detail.setHostType(getNamedValue(rowData, "hostType"));
                            detail.setServiceName(getNamedValue(rowData, "serviceName"));
                            detail.setLambdaNames(getNamedValue(rowData, "lambdaNames"));
                            detail.setS3Bucket(getNamedValue(rowData, "s3Bucket"));
                            detail.setSqsNames(getNamedValue(rowData, "sqsNames"));
                            detail.setIamUser(getNamedValue(rowData, "iamUser"));
                            detail.setComments(getNamedValue(rowData, "comments"));
                            
                            log.debug("Detail: env={}, accountId={}", detail.getEnvironment(), detail.getAccountId());
                            
                            // Only save if at least one field has data
                            if (hasData(detail)) {
                                CloudDetail saved = repository.save(detail);
                                log.debug("Saved CloudDetail ID: {}", saved.getId());
                                savedRecords.add(saved);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("ERROR in CloudDetailService.processBulkGridData: {}", e.getMessage(), e);
        }

        log.info("=== CLOUD DETAIL: Total saved records: {} ===", savedRecords.size());
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
