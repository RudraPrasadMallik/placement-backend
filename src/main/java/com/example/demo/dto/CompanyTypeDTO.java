package com.example.demo.dto;

public class CompanyTypeDTO {
    private String name;
    private Integer value;

    public CompanyTypeDTO() {}

    public CompanyTypeDTO(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
