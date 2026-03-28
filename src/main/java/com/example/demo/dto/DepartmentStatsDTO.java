package com.example.demo.dto;

public class DepartmentStatsDTO {
    private String department;
    private Integer placed;
    private Integer total;

    public DepartmentStatsDTO() {}

    public DepartmentStatsDTO(String department, Integer placed, Integer total) {
        this.department = department;
        this.placed = placed;
        this.total = total;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getPlaced() {
        return placed;
    }

    public Integer getTotal() {
        return total;
    }
}
