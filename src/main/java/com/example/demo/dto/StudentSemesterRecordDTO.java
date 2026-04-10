package com.example.demo.dto;

public class StudentSemesterRecordDTO {
    private Long id;
    private String semesterName;
    private Double percentage;
    private String marksheetUrl;
    private String marksheetName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
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
