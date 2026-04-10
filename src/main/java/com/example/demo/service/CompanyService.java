package com.example.demo.service;

import com.example.demo.dto.CompanyProfileDTO;
import com.example.demo.dto.CompanyProfileUpdateDTO;
import com.example.demo.dto.CompanyRegistrationDTO;
import com.example.demo.exception.FieldValidationException;
import com.example.demo.model.Company;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class CompanyService {
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,15}$";
    private static final String JD_UPLOAD_DIR = "uploads/jds/";

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Company registerCompany(CompanyRegistrationDTO dto, MultipartFile jdFile) throws Exception {
        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        String rawPassword = dto.getPassword() == null ? "" : dto.getPassword().trim();
        String trimmedCompanyName = dto.getCompanyName().trim();
        String trimmedIndustry = dto.getIndustry().trim();
        String trimmedContactPerson = dto.getContactPerson().trim();
        String trimmedPhone = dto.getPhone().trim();
        String trimmedWebsite = trimToNull(dto.getWebsite());
        String trimmedSalaryPackage = trimToNull(dto.getSalaryPackage());
        Double minimumCgpa = dto.getMinimumCgpa();
        String trimmedSemester = trimToNull(dto.getSemester());
        String trimmedLocation = trimToNull(dto.getLocation());
        String trimmedJobDescription = trimToNull(dto.getJobDescription());
        String jdFileUrl = jdFile == null ? null : storeOptionalJdFile(jdFile);

        boolean companyExists = companyRepository.existsByEmail(normalizedEmail);
        boolean userExists = userRepository.existsByEmail(normalizedEmail);

        if (companyExists && userExists) {
            throw new FieldValidationException("email", "Company with this email is already registered");
        }

        if (rawPassword.isBlank()) {
            throw new FieldValidationException("password", "Password is required");
        }

        if (rawPassword.length() < 8 || rawPassword.length() > 15) {
            throw new FieldValidationException("password", "Password must be between 8 and 15 characters");
        }

        if (!rawPassword.matches(PASSWORD_PATTERN)) {
            throw new FieldValidationException(
                "password",
                "Password must include 1 uppercase letter, 1 number, and 1 special character"
            );
        }

        if (companyExists && !userExists) {
            Company existingCompany = companyRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new RuntimeException("Company record exists but could not be loaded"));

            existingCompany.setCompanyName(trimmedCompanyName);
            existingCompany.setIndustry(trimmedIndustry);
            existingCompany.setContactPerson(trimmedContactPerson);
            existingCompany.setPhone(trimmedPhone);
            existingCompany.setWebsite(trimmedWebsite);
            existingCompany.setPositions(dto.getPositions());
            existingCompany.setSalaryPackage(trimmedSalaryPackage);
            existingCompany.setMinimumCgpa(minimumCgpa);
            existingCompany.setSemester(trimmedSemester);
            existingCompany.setLocation(trimmedLocation);
            existingCompany.setJobDescription(trimmedJobDescription);
            existingCompany.setJdFileUrl(jdFileUrl != null ? jdFileUrl : existingCompany.getJdFileUrl());

            Company savedCompany = companyRepository.save(existingCompany);
            createCompanyUser(savedCompany, rawPassword);
            return savedCompany;
        }

        if (!companyExists && userExists) {
            throw new FieldValidationException("email", "Email is already registered");
        }

        Company company = new Company();
        company.setCompanyName(trimmedCompanyName);
        company.setIndustry(trimmedIndustry);
        company.setContactPerson(trimmedContactPerson);
        company.setEmail(normalizedEmail);
        company.setPhone(trimmedPhone);
        company.setWebsite(trimmedWebsite);
        company.setPositions(dto.getPositions());
        company.setSalaryPackage(trimmedSalaryPackage);
        company.setMinimumCgpa(minimumCgpa);
        company.setSemester(trimmedSemester);
        company.setLocation(trimmedLocation);
        company.setJobDescription(trimmedJobDescription);
        company.setJdFileUrl(jdFileUrl);

        Company savedCompany = companyRepository.save(company);
        createCompanyUser(savedCompany, rawPassword);
        return savedCompany;
    }

    public CompanyProfileDTO getCompanyProfileByEmail(String email) {
        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company not found with email: " + email));

        return convertToCompanyProfileDTO(company);
    }

    public Company getCompanyByEmail(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company not found with email: " + email));
    }

    public CompanyProfileDTO updateCompanyProfile(String email, CompanyProfileUpdateDTO dto) {
        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company not found with email: " + email));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        company.setCompanyName(dto.getCompanyName().trim());
        company.setIndustry(trimToNull(dto.getIndustry()));
        company.setContactPerson(dto.getContactPerson().trim());
        company.setWebsite(trimToNull(dto.getWebsite()));
        company.setPositions(dto.getPositions());
        company.setSalaryPackage(trimToNull(dto.getSalaryPackage()));
        company.setMinimumCgpa(dto.getMinimumCgpa());
        company.setSemester(trimToNull(dto.getSemester()));
        company.setLocation(trimToNull(dto.getLocation()));
        company.setJobDescription(trimToNull(dto.getJobDescription()));
        user.setFullName(dto.getCompanyName().trim());

        Company savedCompany = companyRepository.save(company);
        userRepository.save(user);
        return convertToCompanyProfileDTO(savedCompany);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private CompanyProfileDTO convertToCompanyProfileDTO(Company company) {
        CompanyProfileDTO dto = new CompanyProfileDTO();
        dto.setId(company.getId());
        dto.setCompanyName(company.getCompanyName());
        dto.setIndustry(company.getIndustry());
        dto.setContactPerson(company.getContactPerson());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setWebsite(company.getWebsite());
        dto.setPositions(company.getPositions());
        dto.setSalaryPackage(company.getSalaryPackage());
        dto.setMinimumCgpa(company.getMinimumCgpa());
        dto.setSemester(company.getSemester());
        dto.setLocation(company.getLocation());
        dto.setJobDescription(company.getJobDescription());
        dto.setJdFileUrl(company.getJdFileUrl());
        dto.setRegistrationStatus(company.getRegistrationStatus());
        return dto;
    }

    private String storeOptionalJdFile(MultipartFile jdFile) throws Exception {
        if (jdFile == null || jdFile.isEmpty()) {
            return null;
        }

        File dir = new File(JD_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = jdFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new FieldValidationException("jdFile", "JD must be a PDF file");
        }

        String fileName = UUID.randomUUID() + "_" + Path.of(originalFilename).getFileName();
        String filePath = JD_UPLOAD_DIR + fileName;
        File destination = new File(dir.getAbsolutePath(), fileName);

        try {
            jdFile.transferTo(destination);
        } catch (IOException e) {
            throw new Exception("JD upload failed: " + e.getMessage());
        }

        return filePath;
    }

    private void createCompanyUser(Company company, String rawPassword) {
        User user = new User();
        user.setEmail(company.getEmail());
        user.setUsername(company.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(company.getCompanyName());
        user.setPhoneNumber(company.getPhone());
        user.setRole(Role.COMPANY);
        user.setActive(false);
        userRepository.save(user);
    }

}
