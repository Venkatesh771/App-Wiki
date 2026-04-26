package com.example.dictionary.service;

import com.example.dictionary.entity.DropdownOption;
import com.example.dictionary.repository.DropdownOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DropdownOptionService {

    @Autowired
    private DropdownOptionRepository repo;

    public List<String> getValues(String fieldName) {
        return repo.findByFieldNameOrderByValueAsc(fieldName)
                .stream()
                .map(DropdownOption::getValue)
                .collect(Collectors.toList());
    }

    /** Returns true if saved, false if a duplicate already exists. */
    public boolean save(String fieldName, String value) {
        if (repo.existsByFieldNameAndValueIgnoreCase(fieldName, value)) {
            return false;
        }
        DropdownOption opt = new DropdownOption();
        opt.setFieldName(fieldName);
        opt.setValue(value);
        repo.save(opt);
        return true;
    }
}
