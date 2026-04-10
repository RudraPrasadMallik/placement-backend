package com.example.demo.service;

import com.example.demo.dto.CreateJobPostingDTO;
import com.example.demo.dto.JobPostingDTO;
import com.example.demo.dto.PublicCompanyJobsDTO;
import com.example.demo.model.Company;
import com.example.demo.model.JobPosting;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobPostingRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobPostingService {
    private static final String JOB_JD_UPLOAD_DIR = "uploads/job-jds/";
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;

    public JobPostingService(
        JobPostingRepository jobPostingRepository,
        CompanyRepository companyRepository
    ) {
        this.jobPostingRepository = jobPostingRepository;
        this.companyRepository = companyRepository;
    }

    public List<JobPostingDTO> getCompanyJobs(String email) {
        Company company = companyRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Company not found with email: " + email));

        return jobPostingRepository.findByCompanyIdOrderByCreatedAtDesc(company.getId())
            .stream()
            .map(this::convertToJobPostingDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public JobPostingDTO createJobPosting(String email, CreateJobPostingDTO dto, MultipartFile jdFile) throws Exception {
        Company company = companyRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Company not found with email: " + email));

        JobPosting jobPosting = new JobPosting();
        jobPosting.setCompany(company);
        jobPosting.setTitle(dto.getTitle().trim());
        jobPosting.setDescription(dto.getDescription().trim());
        jobPosting.setPositions(dto.getPositions());
        jobPosting.setSalaryPackage(dto.getSalaryPackage().trim());
        jobPosting.setMinimumCgpa(dto.getMinimumCgpa());
        jobPosting.setSemester(trimToNull(dto.getSemester()));
        jobPosting.setLocation(dto.getLocation().trim());
        jobPosting.setApplicationLink(trimToNull(dto.getApplicationLink()));
        jobPosting.setJdFileUrl(storeOptionalJdFile(jdFile));
        jobPosting.setStatus("PENDING");

        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return convertToJobPostingDTO(savedJobPosting);
    }

    public List<PublicCompanyJobsDTO> getApprovedCompanyJobs() {
        List<JobPosting> approvedJobs = jobPostingRepository
            .findByCompanyRegistrationStatusIgnoreCaseAndStatusIgnoreCaseOrderByCreatedAtDesc("APPROVED", "APPROVED");

        Map<Long, PublicCompanyJobsDTO> groupedCompanies = new LinkedHashMap<>();

        approvedJobs.stream()
            .sorted(Comparator
                .comparing((JobPosting job) -> job.getCompany().getCompanyName(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(JobPosting::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .forEach(jobPosting -> {
                Company company = jobPosting.getCompany();
                PublicCompanyJobsDTO companyJobs = groupedCompanies.computeIfAbsent(company.getId(), companyId -> {
                    PublicCompanyJobsDTO dto = new PublicCompanyJobsDTO();
                    dto.setCompanyId(company.getId());
                    dto.setCompanyName(company.getCompanyName());
                    dto.setIndustry(company.getIndustry());
                    dto.setWebsite(company.getWebsite());
                    dto.setLocation(company.getLocation());
                    dto.setContactPerson(company.getContactPerson());
                    dto.setJobs(new java.util.ArrayList<>());
                    return dto;
                });

                companyJobs.getJobs().add(convertToJobPostingDTO(jobPosting));
            });

        return groupedCompanies.values().stream().collect(Collectors.toList());
    }

    public JobPostingDTO convertToJobPostingDTO(JobPosting jobPosting) {
        JobPostingDTO dto = new JobPostingDTO();
        dto.setId(jobPosting.getId());
        dto.setCompanyId(jobPosting.getCompany().getId());
        dto.setCompanyName(jobPosting.getCompany().getCompanyName());
        dto.setTitle(jobPosting.getTitle());
        dto.setDescription(jobPosting.getDescription());
        dto.setPositions(jobPosting.getPositions());
        dto.setSalaryPackage(jobPosting.getSalaryPackage());
        dto.setMinimumCgpa(jobPosting.getMinimumCgpa());
        dto.setSemester(jobPosting.getSemester());
        dto.setLocation(jobPosting.getLocation());
        dto.setApplicationLink(jobPosting.getApplicationLink());
        dto.setJdFileUrl(jobPosting.getJdFileUrl());
        dto.setStatus(jobPosting.getStatus());
        dto.setCreatedAt(jobPosting.getCreatedAt());
        return dto;
    }

    private String storeOptionalJdFile(MultipartFile jdFile) throws Exception {
        if (jdFile == null || jdFile.isEmpty()) {
            return null;
        }

        File dir = new File(JOB_JD_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = jdFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("JD must be a PDF file");
        }

        String fileName = java.util.UUID.randomUUID() + "_" + Path.of(originalFilename).getFileName();
        File destination = new File(dir.getAbsolutePath(), fileName);

        try {
            jdFile.transferTo(destination);
        } catch (IOException e) {
            throw new Exception("JD upload failed: " + e.getMessage());
        }

        return JOB_JD_UPLOAD_DIR + fileName;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
