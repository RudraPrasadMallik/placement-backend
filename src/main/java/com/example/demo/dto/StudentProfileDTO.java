package com.example.demo.dto;

public class StudentProfileDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String rollNumber;
    private String department;
    private String year;
    private Double cgpa;
    private String resumeUrl;
    private String resumeName;
    private String semesterName;
    private Double semesterPercentage;
    private String marksheetUrl;
    private String marksheetName;
    private String placementStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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

    public String getPlacementStatus() {
        return placementStatus;
    }

    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }

    public String getResumeName() {
        return resumeName;
    }

    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
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

    public String getMarksheetName() {
        return marksheetName;
    }

    public void setMarksheetName(String marksheetName) {
        this.marksheetName = marksheetName;
    }
}
