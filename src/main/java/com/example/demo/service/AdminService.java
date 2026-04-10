package com.example.demo.service;

import com.example.demo.dto.AdminDashboardDTO;
import com.example.demo.dto.AdminStudentDTO;
import com.example.demo.dto.AdminCompanyDTO;
import com.example.demo.dto.AdminJobPostingDTO;
import com.example.demo.dto.StudentAppliedJobDTO;
import com.example.demo.dto.StudentSemesterRecordDTO;
import com.example.demo.model.Student;
import com.example.demo.model.Company;
import com.example.demo.model.JobPosting;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobPostingRepository;
import com.example.demo.repository.StudentJobApplicationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentJobApplicationRepository studentJobApplicationRepository;

    @Autowired
    private StudentService studentService;

    public AdminDashboardDTO getDashboardData() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        
        // Student statistics
        List<Student> allStudents = studentRepository.findAll();
        dashboard.setTotalStudents(allStudents.size());
        dashboard.setActiveStudents((int) allStudents.stream().filter(Student::isActive).count());
        
        // Company statistics
        List<Company> allCompanies = companyRepository.findAll();
        dashboard.setTotalCompanies(allCompanies.size());
        dashboard.setActiveCompanies((int) allCompanies.stream().filter(c -> "APPROVED".equals(c.getRegistrationStatus())).count());
        dashboard.setPendingCompanies((int) allCompanies.stream().filter(c -> "PENDING".equalsIgnoreCase(c.getRegistrationStatus())).count());

        // Job statistics
        List<JobPosting> allJobs = jobPostingRepository.findAll();
        dashboard.setTotalJobs(allJobs.size());
        dashboard.setApprovedJobs((int) allJobs.stream().filter(job -> "APPROVED".equalsIgnoreCase(job.getStatus())).count());
        dashboard.setPendingJobs((int) allJobs.stream().filter(job -> "PENDING".equalsIgnoreCase(job.getStatus())).count());
        
        // Recent registrations (last 10)
        dashboard.setRecentStudents(getRecentStudents(10));
        dashboard.setRecentCompanies(getRecentCompanies(10));
        
        return dashboard;
    }

    public List<AdminStudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToAdminStudentDTO)
                .collect(Collectors.toList());
    }

    public List<AdminCompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::convertToAdminCompanyDTO)
                .collect(Collectors.toList());
    }

    public List<AdminJobPostingDTO> getAllJobPostings() {
        return jobPostingRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(this::convertToAdminJobPostingDTO)
            .collect(Collectors.toList());
    }

    public List<AdminStudentDTO> getRecentStudents(int limit) {
        return studentRepository.findAll().stream()
                .sorted((s1, s2) -> s2.getId().compareTo(s1.getId())) // Sort by ID descending (assuming ID is auto-increment)
                .limit(limit)
                .map(this::convertToAdminStudentDTO)
                .collect(Collectors.toList());
    }

    public List<AdminCompanyDTO> getRecentCompanies(int limit) {
        return companyRepository.findAll().stream()
                .sorted((c1, c2) -> c2.getId().compareTo(c1.getId())) // Sort by ID descending
                .limit(limit)
                .map(this::convertToAdminCompanyDTO)
                .collect(Collectors.toList());
    }

    public AdminStudentDTO toggleStudentStatus(Long studentId, boolean active) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setActive(active);
        Student updatedStudent = studentRepository.save(student);
        
        return convertToAdminStudentDTO(updatedStudent);
    }

    public AdminCompanyDTO toggleCompanyStatus(Long companyId, boolean active) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
        
        company.setRegistrationStatus(active ? "APPROVED" : "REJECTED");
        Company updatedCompany = companyRepository.save(company);
        userRepository.findByEmail(updatedCompany.getEmail()).ifPresent(user -> {
            user.setActive(active);
            userRepository.save(user);
        });
        
        return convertToAdminCompanyDTO(updatedCompany);
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found with id: " + studentId);
        }
        studentJobApplicationRepository.deleteByStudentId(studentId);
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        studentJobApplicationRepository.deleteByJobPostingCompanyId(companyId);
        jobPostingRepository.deleteByCompanyId(companyId);
        userRepository.deleteByEmail(company.getEmail());
        companyRepository.deleteById(companyId);
    }

    public AdminJobPostingDTO toggleJobStatus(Long jobId, boolean active) {
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        jobPosting.setStatus(active ? "APPROVED" : "REJECTED");
        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);

        return convertToAdminJobPostingDTO(updatedJobPosting);
    }

    private AdminStudentDTO convertToAdminStudentDTO(Student student) {
        AdminStudentDTO dto = new AdminStudentDTO();
        dto.setId(student.getId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setRollNumber(student.getRollNumber());
        dto.setDepartment(student.getDepartment());
        dto.setDegree(student.getDepartment()); // Using department as degree since degree field doesn't exist
        dto.setGraduationYear(parseGraduationYear(student.getYear()));
        dto.setCurrentCgpa(student.getCgpa());
        dto.setActive(student.isActive());
        dto.setResumeUrl(student.getResumeUrl());
        dto.setResumeFileName(student.getResumeUrl() == null ? null : Path.of(student.getResumeUrl()).getFileName().toString());
        dto.setSemesterName(student.getSemesterName());
        dto.setSemesterPercentage(student.getSemesterPercentage());
        dto.setMarksheetUrl(student.getMarksheetUrl());
        dto.setMarksheetFileName(student.getMarksheetUrl() == null ? null : Path.of(student.getMarksheetUrl()).getFileName().toString());
        List<StudentSemesterRecordDTO> semesterRecords = studentService.getStudentSemesterRecords(student.getId());
        dto.setSemesterRecords(semesterRecords);
        dto.setRegistrationDate(student.getCreatedAt());
        List<StudentAppliedJobDTO> appliedJobs = studentService.getAppliedJobs(student.getId());
        dto.setAppliedJobs(appliedJobs);
        return dto;
    }

    private AdminCompanyDTO convertToAdminCompanyDTO(Company company) {
        AdminCompanyDTO dto = new AdminCompanyDTO();
        dto.setId(company.getId());
        dto.setCompanyName(company.getCompanyName());
        dto.setIndustry(company.getIndustry());
        dto.setDescription(company.getJobDescription());
        dto.setWebsite(company.getWebsite());
        dto.setHeadquarters(company.getLocation());
        dto.setCompanySize("Unknown");
        dto.setContactPerson(company.getContactPerson());
        dto.setContactEmail(company.getEmail());
        dto.setContactPhone(company.getPhone());
        dto.setActive("APPROVED".equals(company.getRegistrationStatus()));
        dto.setRegistrationStatus(company.getRegistrationStatus());
        dto.setJobsCount(jobPostingRepository.countByCompanyId(company.getId()));
        dto.setLogoFileName(null);
        dto.setRegistrationDate(company.getRegisteredAt() == null ? LocalDateTime.now() : company.getRegisteredAt());
        return dto;
    }

    private AdminJobPostingDTO convertToAdminJobPostingDTO(JobPosting jobPosting) {
        AdminJobPostingDTO dto = new AdminJobPostingDTO();
        dto.setId(jobPosting.getId());
        dto.setCompanyId(jobPosting.getCompany().getId());
        dto.setCompanyName(jobPosting.getCompany().getCompanyName());
        dto.setTitle(jobPosting.getTitle());
        dto.setDescription(jobPosting.getDescription());
        dto.setPositions(jobPosting.getPositions());
        dto.setSalaryPackage(jobPosting.getSalaryPackage());
        dto.setMinimumCgpa(jobPosting.getMinimumCgpa());
        dto.setLocation(jobPosting.getLocation());
        dto.setApplicationLink(jobPosting.getApplicationLink());
        dto.setJdFileUrl(jobPosting.getJdFileUrl());
        dto.setStatus(jobPosting.getStatus());
        dto.setCreatedAt(jobPosting.getCreatedAt());
        return dto;
    }

    private int parseGraduationYear(String yearValue) {
        if (yearValue == null || yearValue.isBlank()) {
            return 0;
        }

        String digits = yearValue.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
