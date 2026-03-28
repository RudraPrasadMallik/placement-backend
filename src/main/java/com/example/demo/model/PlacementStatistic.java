package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "placement_statistics")
public class PlacementStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String year;
    private Integer placed;
    private Integer total;
    private Double avgPackage;

    public Long getId() {
        return id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getPlaced() {
        return placed;
    }

    public void setPlaced(Integer placed) {
        this.placed = placed;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getAvgPackage() {
        return avgPackage;
    }

    public void setAvgPackage(Double avgPackage) {
        this.avgPackage = avgPackage;
    }
}
