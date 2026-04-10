package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CompanyRegistrationDTO {
    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must be at most 255 characters")
    private String companyName;

    @NotBlank(message = "Industry is required")
    @Size(max = 255, message = "Industry must be at most 255 characters")
    private String industry;

    @NotBlank(message = "Contact person is required")
    @Size(max = 255, message = "Contact person must be at most 255 characters")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,15}$",
        message = "Password must include 1 uppercase letter, 1 number, and 1 special character"
    )
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+()\\-\\s]{7,20}$", message = "Phone number must be valid")
    private String phone;

    @Size(max = 255, message = "Website must be at most 255 characters")
    private String website;

    private Integer positions;

    @Size(max = 255, message = "Salary package must be at most 255 characters")
    private String salaryPackage;

    @Min(value = 0, message = "Minimum CGPA must be at least 0")
    private Double minimumCgpa;

    @Size(max = 50, message = "Semester must be at most 50 characters")
    private String semester;

    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @Size(max = 2000, message = "Job description must be at most 2000 characters")
    private String jobDescription;

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Integer getPositions() { return positions; }
    public void setPositions(Integer positions) { this.positions = positions; }

    public String getSalaryPackage() { return salaryPackage; }
    public void setSalaryPackage(String salaryPackage) { this.salaryPackage = salaryPackage; }

    public Double getMinimumCgpa() { return minimumCgpa; }
    public void setMinimumCgpa(Double minimumCgpa) { this.minimumCgpa = minimumCgpa; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
}
