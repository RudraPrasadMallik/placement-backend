package com.example.demo.dto;

import java.util.List;

public class AdminDashboardDTO {
    private int totalStudents;
    private int activeStudents;
    private int totalCompanies;
    private int activeCompanies;
    private int pendingCompanies;
    private int totalJobs;
    private int approvedJobs;
    private int pendingJobs;
    private List<AdminStudentDTO> recentStudents;
    private List<AdminCompanyDTO> recentCompanies;

    // Getters and Setters
    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getActiveStudents() {
        return activeStudents;
    }

    public void setActiveStudents(int activeStudents) {
        this.activeStudents = activeStudents;
    }

    public int getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(int totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public int getActiveCompanies() {
        return activeCompanies;
    }

    public void setActiveCompanies(int activeCompanies) {
        this.activeCompanies = activeCompanies;
    }

    public List<AdminStudentDTO> getRecentStudents() {
        return recentStudents;
    }

    public void setRecentStudents(List<AdminStudentDTO> recentStudents) {
        this.recentStudents = recentStudents;
    }

    public List<AdminCompanyDTO> getRecentCompanies() {
        return recentCompanies;
    }

    public void setRecentCompanies(List<AdminCompanyDTO> recentCompanies) {
        this.recentCompanies = recentCompanies;
    }

    public int getPendingCompanies() {
        return pendingCompanies;
    }

    public void setPendingCompanies(int pendingCompanies) {
        this.pendingCompanies = pendingCompanies;
    }

    public int getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(int totalJobs) {
        this.totalJobs = totalJobs;
    }

    public int getApprovedJobs() {
        return approvedJobs;
    }

    public void setApprovedJobs(int approvedJobs) {
        this.approvedJobs = approvedJobs;
    }

    public int getPendingJobs() {
        return pendingJobs;
    }

    public void setPendingJobs(int pendingJobs) {
        this.pendingJobs = pendingJobs;
    }
}
