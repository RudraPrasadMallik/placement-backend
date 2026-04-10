package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class StudentProfileUpdateDTO {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private String department;

    private String year;

    @DecimalMin(value = "0.0", message = "CGPA must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "CGPA must be between 0.0 and 10.0")
    private Double cgpa;

    private String semesterName;

    @DecimalMin(value = "0.0", message = "Semester percentage must be between 0.0 and 100.0")
    @DecimalMax(value = "100.0", message = "Semester percentage must be between 0.0 and 100.0")
    private Double semesterPercentage;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Double getSemesterPercentage() {
        return semesterPercentage;
    }

    public void setSemesterPercentage(Double semesterPercentage) {
        this.semesterPercentage = semesterPercentage;
    }
}
