package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateRegistrationOptionDTO {
    @NotBlank(message = "Value is required")
    private String value;

    private Integer sortOrder;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
