package com.example.demo.service;

import com.example.demo.dto.CompanyRegistrationDTO;
import com.example.demo.model.Company;
import com.example.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company registerCompany(CompanyRegistrationDTO dto) {
        // Check if company already exists
        if (companyRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Company with this email already registered");
        }

        Company company = new Company();
        company.setCompanyName(dto.getCompanyName());
        company.setIndustry(dto.getIndustry());
        company.setContactPerson(dto.getContactPerson());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());
        company.setWebsite(dto.getWebsite());
        company.setPositions(dto.getPositions());
        company.setSalaryPackage(dto.getSalaryPackage());
        company.setLocation(dto.getLocation());
        company.setJobDescription(dto.getJobDescription());

        return companyRepository.save(company);
    }
}