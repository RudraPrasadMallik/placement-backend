package com.example.demo.dto;

public class PackageDistributionDTO {
    private String range;
    private Integer count;

    public PackageDistributionDTO() {}

    public PackageDistributionDTO(String range, Integer count) {
        this.range = range;
        this.count = count;
    }

    public String getRange() {
        return range;
    }

    public Integer getCount() {
        return count;
    }
}
