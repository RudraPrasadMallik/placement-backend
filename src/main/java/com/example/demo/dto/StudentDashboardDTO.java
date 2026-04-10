package com.example.demo.dto;

import java.util.List;

public class StudentDashboardDTO {
    private StudentProfileDTO student;
    private List<CompanyOpportunityDTO> availableCompanies;
    private List<StudentAppliedJobDTO> appliedJobs;
    private int totalAvailableCompanies;
    private int totalAppliedJobs;

    public StudentProfileDTO getStudent() {
        return student;
    }

    public void setStudent(StudentProfileDTO student) {
        this.student = student;
    }

    public List<CompanyOpportunityDTO> getAvailableCompanies() {
        return availableCompanies;
    }

    public void setAvailableCompanies(List<CompanyOpportunityDTO> availableCompanies) {
        this.availableCompanies = availableCompanies;
    }

    public int getTotalAvailableCompanies() {
        return totalAvailableCompanies;
    }

    public void setTotalAvailableCompanies(int totalAvailableCompanies) {
        this.totalAvailableCompanies = totalAvailableCompanies;
    }

    public List<StudentAppliedJobDTO> getAppliedJobs() {
        return appliedJobs;
    }

    public void setAppliedJobs(List<StudentAppliedJobDTO> appliedJobs) {
        this.appliedJobs = appliedJobs;
    }

    public int getTotalAppliedJobs() {
        return totalAppliedJobs;
    }

    public void setTotalAppliedJobs(int totalAppliedJobs) {
        this.totalAppliedJobs = totalAppliedJobs;
    }
}
