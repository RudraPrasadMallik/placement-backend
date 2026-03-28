package com.example.demo.dto;

public class YearWiseStatsDTO {
    private String year;
    private Integer placed;
    private Integer total;
    private Double avgPackage;

    public YearWiseStatsDTO() {}

    public YearWiseStatsDTO(String year, Integer placed, Integer total, Double avgPackage) {
        this.year = year;
        this.placed = placed;
        this.total = total;
        this.avgPackage = avgPackage;
    }

    public String getYear() {
        return year;
    }

    public Integer getPlaced() {
        return placed;
    }

    public Integer getTotal() {
        return total;
    }

    public Double getAvgPackage() {
        return avgPackage;
    }
}
