package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends User {
    
    private String registrationNumber;
    private String department;
    private String semester;
    private Double cgpa;
    private Integer backlogs;
    private String tenthPercentage;
    private String twelfthPercentage;
    private String graduationPercentage;
    private String skills;
    private String resumeUrl;
    private String address;
    
    private String placementStatus = "NOT_PLACED"; // NOT_PLACED, PLACED, INTERVIEW_SCHEDULED
    
    // Getters and Setters
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public Double getCgpa() {
        return cgpa;
    }
    
    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }
    
    public Integer getBacklogs() {
        return backlogs;
    }
    
    public void setBacklogs(Integer backlogs) {
        this.backlogs = backlogs;
    }
    
    public String getTenthPercentage() {
        return tenthPercentage;
    }
    
    public void setTenthPercentage(String tenthPercentage) {
        this.tenthPercentage = tenthPercentage;
    }
    
    public String getTwelfthPercentage() {
        return twelfthPercentage;
    }
    
    public void setTwelfthPercentage(String twelfthPercentage) {
        this.twelfthPercentage = twelfthPercentage;
    }
    
    public String getGraduationPercentage() {
        return graduationPercentage;
    }
    
    public void setGraduationPercentage(String graduationPercentage) {
        this.graduationPercentage = graduationPercentage;
    }
    
    public String getSkills() {
        return skills;
    }
    
    public void setSkills(String skills) {
        this.skills = skills;
    }
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPlacementStatus() {
        return placementStatus;
    }
    
    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }
}