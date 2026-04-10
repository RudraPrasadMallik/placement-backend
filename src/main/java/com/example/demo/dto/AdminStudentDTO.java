package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminStudentDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String rollNumber;
    private String department;
    private String degree;
    private int graduationYear;
    private double currentCgpa;
    private boolean active;
    private LocalDateTime registrationDate;
    private String resumeFileName;
    private String resumeUrl;
    private String semesterName;
    private Double semesterPercentage;
    private String marksheetFileName;
    private String marksheetUrl;
    private List<StudentSemesterRecordDTO> semesterRecords;
    private List<StudentAppliedJobDTO> appliedJobs;

    // Getters and Setters
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public double getCurrentCgpa() {
        return currentCgpa;
    }

    public void setCurrentCgpa(double currentCgpa) {
        this.currentCgpa = currentCgpa;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getResumeFileName() {
        return resumeFileName;
    }

    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
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

    public String getMarksheetFileName() {
        return marksheetFileName;
    }

    public void setMarksheetFileName(String marksheetFileName) {
        this.marksheetFileName = marksheetFileName;
    }

    public String getMarksheetUrl() {
        return marksheetUrl;
    }

    public void setMarksheetUrl(String marksheetUrl) {
        this.marksheetUrl = marksheetUrl;
    }

    public List<StudentSemesterRecordDTO> getSemesterRecords() {
        return semesterRecords;
    }

    public void setSemesterRecords(List<StudentSemesterRecordDTO> semesterRecords) {
        this.semesterRecords = semesterRecords;
    }

    public List<StudentAppliedJobDTO> getAppliedJobs() {
        return appliedJobs;
    }

    public void setAppliedJobs(List<StudentAppliedJobDTO> appliedJobs) {
        this.appliedJobs = appliedJobs;
    }
}
