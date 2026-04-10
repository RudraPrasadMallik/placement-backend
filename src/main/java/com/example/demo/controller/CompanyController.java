package com.example.demo.controller;

import com.example.demo.dto.CompanyProfileDTO;
import com.example.demo.dto.CompanyProfileUpdateDTO;
import com.example.demo.dto.CompanyRegistrationDTO;
import com.example.demo.dto.CreateJobPostingDTO;
import com.example.demo.dto.JobPostingDTO;
import com.example.demo.model.Company;
import com.example.demo.service.CompanyService;
import com.example.demo.service.JobPostingService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping({"/api/companies", "/companies"})
@CrossOrigin(origins = "*")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobPostingService jobPostingService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerCompany(
            @RequestParam("companyName") String companyName,
            @RequestParam("industry") String industry,
            @RequestParam("contactPerson") String contactPerson,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "positions", required = false) Integer positions,
            @RequestParam(value = "salaryPackage", required = false) String salaryPackage,
            @RequestParam(value = "minimumCgpa", required = false) Double minimumCgpa,
            @RequestParam(value = "semester", required = false) String semester,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "jobDescription", required = false) String jobDescription
    ) throws Exception {

        CompanyRegistrationDTO dto = new CompanyRegistrationDTO();
        dto.setCompanyName(companyName);
        dto.setIndustry(industry);
        dto.setContactPerson(contactPerson);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setPhone(phone);
        dto.setWebsite(website);
        dto.setPositions(positions);
        dto.setSalaryPackage(salaryPackage);
        dto.setMinimumCgpa(minimumCgpa);
        dto.setSemester(semester);
        dto.setLocation(location);
        dto.setJobDescription(jobDescription);

        Set<ConstraintViolation<CompanyRegistrationDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> fieldErrors = new HashMap<>();

            for (ConstraintViolation<CompanyRegistrationDTO> violation : violations) {
                String fieldName = violation.getPropertyPath().toString();
                fieldErrors.putIfAbsent(fieldName, violation.getMessage());
            }

            response.put("status", "error");
            response.put("message", "Validation failed");
            response.put("fieldErrors", fieldErrors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        companyService.registerCompany(dto, null);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration submitted successfully! We'll contact you soon.");
        response.put("status", "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getLoggedInCompanyProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in to access the company profile."));
        }

        boolean isCompany = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_COMPANY".equals(authority.getAuthority()));

        if (!isCompany) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only company accounts can access this profile."));
        }

        CompanyProfileDTO profile = companyService.getCompanyProfileByEmail(authentication.getName());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentCompany(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in to access the company profile."));
        }

        boolean isCompany = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_COMPANY".equals(authority.getAuthority()));

        if (!isCompany) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only company accounts can access this profile."));
        }

        Company company = companyService.getCompanyByEmail(authentication.getName());
        CompanyProfileDTO profile = companyService.getCompanyProfileByEmail(company.getEmail());
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateLoggedInCompanyProfile(
            Authentication authentication,
            @Valid @RequestBody CompanyProfileUpdateDTO dto
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in to update the company profile."));
        }

        boolean isCompany = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_COMPANY".equals(authority.getAuthority()));

        if (!isCompany) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only company accounts can update this profile."));
        }

        CompanyProfileDTO profile = companyService.updateCompanyProfile(authentication.getName(), dto);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getLoggedInCompanyJobs(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please log in to access company jobs."));
        }

        boolean isCompany = authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_COMPANY".equals(authority.getAuthority()));

        if (!isCompany) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Only company accounts can access company jobs."));
        }

        List<JobPostingDTO> jobs = jobPostingService.getCompanyJobs(authentication.getName());
        return ResponseEntity.ok(jobs);
    }

    @PostMapping(value = "/jobs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createJobPosting(
        Authentication authentication,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("positions") Integer positions,
        @RequestParam("salaryPackage") String salaryPackage,
        @RequestParam(value = "minimumCgpa", required = false) Double minimumCgpa,
        @RequestParam(value = "semester", required = false) String semester,
        @RequestParam("location") String location,
        @RequestParam(value = "applicationLink", required = false) String applicationLink,
        @RequestParam(value = "jdFile", required = false) MultipartFile jdFile
    ) throws Exception {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please log in to create a job posting."));
        }

        boolean isCompany = authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_COMPANY".equals(authority.getAuthority()));

        if (!isCompany) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Only company accounts can create job postings."));
        }

        if (jdFile != null && !jdFile.isEmpty()) {
            String contentType = jdFile.getContentType();
            String originalFilename = jdFile.getOriginalFilename();
            boolean isPdfContentType = "application/pdf".equalsIgnoreCase(contentType);
            boolean hasPdfExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf");

            if (!isPdfContentType && !hasPdfExtension) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "field", "jdFile",
                    "message", "Only PDF files are allowed for JD upload"
                ));
            }

            if (jdFile.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "field", "jdFile",
                    "message", "JD file size must not exceed 5MB"
                ));
            }
        }

        CreateJobPostingDTO dto = new CreateJobPostingDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setPositions(positions);
        dto.setSalaryPackage(salaryPackage);
        dto.setMinimumCgpa(minimumCgpa);
        dto.setSemester(semester);
        dto.setLocation(location);
        dto.setApplicationLink(applicationLink);

        Set<ConstraintViolation<CreateJobPostingDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> fieldErrors = new HashMap<>();

            for (ConstraintViolation<CreateJobPostingDTO> violation : violations) {
                String fieldName = violation.getPropertyPath().toString();
                fieldErrors.putIfAbsent(fieldName, violation.getMessage());
            }

            response.put("status", "error");
            response.put("message", "Validation failed");
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        JobPostingDTO jobPosting = jobPostingService.createJobPosting(authentication.getName(), dto, jdFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobPosting);
    }
}
