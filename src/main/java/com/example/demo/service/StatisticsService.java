package com.example.demo.service;

import com.example.demo.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    public List<YearWiseStatsDTO> getYearWiseStats() {
        return List.of(
                new YearWiseStatsDTO("2021-22", 420, 450, 8.5),
                new YearWiseStatsDTO("2022-23", 485, 520, 10.2),
                new YearWiseStatsDTO("2023-24", 512, 550, 11.5),
                new YearWiseStatsDTO("2024-25", 548, 580, 12.0),
                new YearWiseStatsDTO("2025-26", 587, 620, 13.2)
        );
    }

    public List<DepartmentStatsDTO> getDepartmentStats() {
        return List.of(
                new DepartmentStatsDTO("CSE", 145, 150),
                new DepartmentStatsDTO("ECE", 118, 130),
                new DepartmentStatsDTO("EEE", 95, 110),
                new DepartmentStatsDTO("ME", 88, 100),
                new DepartmentStatsDTO("CE", 75, 90),
                new DepartmentStatsDTO("MCA", 90, 100),
                new DepartmentStatsDTO("MBA", 70, 85)
        );
    }

    public List<PackageDistributionDTO> getPackageDistribution() {
        return List.of(
                new PackageDistributionDTO("0-5 LPA", 85),
                new PackageDistributionDTO("5-10 LPA", 180),
                new PackageDistributionDTO("10-15 LPA", 195),
                new PackageDistributionDTO("15-20 LPA", 92),
                new PackageDistributionDTO("20+ LPA", 35),
                new PackageDistributionDTO("30+ LPA", 80)
        );
    }

    public List<CompanyTypeDTO> getCompanyTypeStats() {
        return List.of(
                new CompanyTypeDTO("IT/Software", 45),
                new CompanyTypeDTO("Consulting", 20),
                new CompanyTypeDTO("Core Engineering", 15),
                new CompanyTypeDTO("Finance", 12),
                new CompanyTypeDTO("Others", 8)
        );
    }
}
