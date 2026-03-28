package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/year-wise")
    public List<YearWiseStatsDTO> getYearWiseStats() {
        return statisticsService.getYearWiseStats();
    }

    @GetMapping("/department-wise")
    public List<DepartmentStatsDTO> getDepartmentStats() {
        return statisticsService.getDepartmentStats();
    }

    @GetMapping("/package-distribution")
    public List<PackageDistributionDTO> getPackageDistribution() {
        return statisticsService.getPackageDistribution();
    }

    @GetMapping("/company-types")
    public List<CompanyTypeDTO> getCompanyTypeStats() {
        return statisticsService.getCompanyTypeStats();
    }
}
