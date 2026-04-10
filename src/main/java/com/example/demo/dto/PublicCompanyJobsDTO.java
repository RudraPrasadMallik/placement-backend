package com.example.demo.dto;

import java.util.List;

public class PublicCompanyJobsDTO {
    private Long companyId;
    private String companyName;
    private String industry;
    private String website;
    private String location;
    private String contactPerson;
    private List<JobPostingDTO> jobs;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public List<JobPostingDTO> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobPostingDTO> jobs) {
        this.jobs = jobs;
    }
}
