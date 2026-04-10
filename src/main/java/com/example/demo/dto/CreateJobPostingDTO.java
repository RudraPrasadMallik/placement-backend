package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateJobPostingDTO {
    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Job title must be at most 255 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(max = 2000, message = "Job description must be at most 2000 characters")
    private String description;

    @NotNull(message = "Number of positions is required")
    @Min(value = 1, message = "Positions must be at least 1")
    private Integer positions;

    @NotBlank(message = "Salary package is required")
    @Size(max = 255, message = "Salary package must be at most 255 characters")
    private String salaryPackage;

    @Min(value = 0, message = "Minimum CGPA must be at least 0")
    private Double minimumCgpa;

    @Size(max = 50, message = "Semester must be at most 50 characters")
    private String semester;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @Size(max = 500, message = "Application link must be at most 500 characters")
    private String applicationLink;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPositions() {
        return positions;
    }

    public void setPositions(Integer positions) {
        this.positions = positions;
    }

    public String getSalaryPackage() {
        return salaryPackage;
    }

    public void setSalaryPackage(String salaryPackage) {
        this.salaryPackage = salaryPackage;
    }

    public Double getMinimumCgpa() {
        return minimumCgpa;
    }

    public void setMinimumCgpa(Double minimumCgpa) {
        this.minimumCgpa = minimumCgpa;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getApplicationLink() {
        return applicationLink;
    }

    public void setApplicationLink(String applicationLink) {
        this.applicationLink = applicationLink;
    }
}
