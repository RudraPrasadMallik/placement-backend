package com.example.demo.dto;

import java.util.List;

public class StudentRegistrationOptionsDTO {
    private List<RegistrationOptionDTO> departments;
    private List<RegistrationOptionDTO> years;

    public List<RegistrationOptionDTO> getDepartments() {
        return departments;
    }

    public void setDepartments(List<RegistrationOptionDTO> departments) {
        this.departments = departments;
    }

    public List<RegistrationOptionDTO> getYears() {
        return years;
    }

    public void setYears(List<RegistrationOptionDTO> years) {
        this.years = years;
    }
}
