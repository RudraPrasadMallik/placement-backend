package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student extends User {
    
    private String rollNumber;
    private String department;
    private String year;
    private Double cgpa;
    private String resumeUrl;
    private String semesterName;
    private Double semesterPercentage;
    private String marksheetUrl;
    private String placementStatus = "NOT_PLACED";
    
    public Student() {
        this.setRole(Role.STUDENT);
    }
    
    // Getters and Setters
    public String getRollNumber() {
        return rollNumber;
    }
    
    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
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
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
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

    public String getMarksheetUrl() {
        return marksheetUrl;
    }

    public void setMarksheetUrl(String marksheetUrl) {
        this.marksheetUrl = marksheetUrl;
    }
    
    public String getPlacementStatus() {
        return placementStatus;
    }
    
    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }
}
