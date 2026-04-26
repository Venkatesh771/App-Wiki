package com.example.dictionary.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dropdown_option", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"field_name", "option_value"})
})
public class DropdownOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "option_value", nullable = false)
    private String value;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
