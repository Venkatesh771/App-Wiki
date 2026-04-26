package com.example.dictionary.repository;

import com.example.dictionary.entity.DropdownOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DropdownOptionRepository extends JpaRepository<DropdownOption, Long> {
    List<DropdownOption> findByFieldNameOrderByValueAsc(String fieldName);
    boolean existsByFieldNameAndValueIgnoreCase(String fieldName, String value);
}
