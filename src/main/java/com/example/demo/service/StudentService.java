package com.example.demo.service;

import com.example.demo.dto.StudentRegistrationDTO;
import com.example.demo.dto.CompanyOpportunityDTO;
import com.example.demo.dto.StudentAppliedJobDTO;
import com.example.demo.dto.StudentDashboardDTO;
import com.example.demo.dto.StudentProfileDTO;
import com.example.demo.dto.StudentProfileUpdateDTO;
import com.example.demo.dto.StudentSemesterRecordDTO;
import com.example.demo.exception.FieldValidationException;
import com.example.demo.model.Company;
import com.example.demo.model.JobPosting;
import com.example.demo.model.Student;
import com.example.demo.model.StudentJobApplication;
import com.example.demo.model.StudentSemesterRecord;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobPostingRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.StudentSemesterRecordRepository;
import com.example.demo.repository.StudentJobApplicationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,15}$";
    private static final String RESUME_UPLOAD_DIR = "uploads/resumes/";
    private static final String MARKSHEET_UPLOAD_DIR = "uploads/marksheets/";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private StudentJobApplicationRepository studentJobApplicationRepository;

    @Autowired
    private StudentSemesterRecordRepository studentSemesterRecordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student registerStudent(StudentRegistrationDTO dto, MultipartFile resume) throws Exception {
        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        String trimmedPhone = normalizePhoneNumber(dto.getPhone());
        String trimmedRollNumber = dto.getRollNumber().trim();
        String rawPassword = dto.getPassword() == null ? "" : dto.getPassword().trim();

        // Validate email uniqueness
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new FieldValidationException("email", "Email is already registered!");
        }

        // Validate roll number uniqueness
        if (studentRepository.existsByRollNumber(trimmedRollNumber)) {
            throw new FieldValidationException("rollNumber", "Roll number is already registered!");
        }

        // Validate phone number uniqueness
        if (studentRepository.existsByPhoneNumber(trimmedPhone)) {
            throw new FieldValidationException("phone", "Phone number is already registered!");
        }

        // Validate CGPA range
        if (dto.getCgpa() < 0.0 || dto.getCgpa() > 10.0) {
            throw new FieldValidationException("cgpa", "CGPA must be between 0.0 and 10.0");
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

        // Handle file upload
        String filePath = storeStudentDocumentFile(resume, RESUME_UPLOAD_DIR, "Resume");

        // Create student with user details
        Student student = new Student();
        student.setEmail(normalizedEmail);
        student.setUsername(normalizedEmail);
        student.setPassword(passwordEncoder.encode(rawPassword));
        student.setFullName(dto.getFullName().trim());
        student.setPhoneNumber(trimmedPhone);
        student.setActive(true);
        
        // Set student-specific fields
        student.setRollNumber(trimmedRollNumber);
        student.setDepartment(dto.getDepartment().trim());
        student.setYear(dto.getYear().trim());
        student.setCgpa(dto.getCgpa());
        student.setResumeUrl(filePath);
        student.setPlacementStatus("NOT_PLACED");

        return studentRepository.save(student);
    }

    public StudentDashboardDTO getStudentDashboard(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        List<CompanyOpportunityDTO> availableCompanies = companyRepository
                .findByRegistrationStatusIgnoreCase("APPROVED")
                .stream()
                .sorted(Comparator.comparing(Company::getRegisteredAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::convertToCompanyOpportunityDTO)
                .collect(Collectors.toList());

        StudentDashboardDTO dashboard = new StudentDashboardDTO();
        dashboard.setStudent(convertToStudentProfileDTO(student));
        dashboard.setAvailableCompanies(availableCompanies);
        dashboard.setTotalAvailableCompanies(availableCompanies.size());
        List<StudentAppliedJobDTO> appliedJobs = getAppliedJobs(student.getId());
        dashboard.setAppliedJobs(appliedJobs);
        dashboard.setTotalAppliedJobs(appliedJobs.size());
        return dashboard;
    }

    public StudentProfileDTO getStudentProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        return convertToStudentProfileDTO(student);
    }

    public StudentProfileDTO getStudentProfileByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        return convertToStudentProfileDTO(student);
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
    }

    public StudentAppliedJobDTO applyForJob(String email, Long jobId) {
        Student student = getStudentByEmail(email);
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        boolean isApprovedJob = "APPROVED".equalsIgnoreCase(jobPosting.getStatus())
                && "APPROVED".equalsIgnoreCase(jobPosting.getCompany().getRegistrationStatus());
        if (!isApprovedJob) {
            throw new RuntimeException("This job is not available for student applications.");
        }

        StudentJobApplication application = studentJobApplicationRepository
                .findByStudentIdAndJobPostingId(student.getId(), jobId)
                .orElseGet(() -> {
                    StudentJobApplication created = new StudentJobApplication();
                    created.setStudent(student);
                    created.setJobPosting(jobPosting);
                    return studentJobApplicationRepository.save(created);
                });

        return convertToStudentAppliedJobDTO(application);
    }

    public List<StudentAppliedJobDTO> getAppliedJobsByEmail(String email) {
        Student student = getStudentByEmail(email);
        return getAppliedJobs(student.getId());
    }

    public List<StudentAppliedJobDTO> getAppliedJobs(Long studentId) {
        return studentJobApplicationRepository.findByStudentIdOrderByAppliedAtDesc(studentId).stream()
                .map(this::convertToStudentAppliedJobDTO)
                .collect(Collectors.toList());
    }

    public StudentProfileDTO updateStudentProfile(String email, StudentProfileUpdateDTO dto) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        student.setFullName(dto.getFullName().trim());
        student.setDepartment(dto.getDepartment() == null ? null : dto.getDepartment().trim());
        student.setYear(dto.getYear() == null ? null : dto.getYear().trim());
        student.setCgpa(dto.getCgpa());
        student.setSemesterName(dto.getSemesterName() == null ? null : dto.getSemesterName().trim());
        student.setSemesterPercentage(dto.getSemesterPercentage());

        Student savedStudent = studentRepository.save(student);
        return convertToStudentProfileDTO(savedStudent);
    }

    public StudentProfileDTO updateStudentResume(String email, MultipartFile resume) throws Exception {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        String previousResumePath = student.getResumeUrl();
        String newResumePath = storeStudentDocumentFile(resume, RESUME_UPLOAD_DIR, "Resume");

        student.setResumeUrl(newResumePath);
        Student savedStudent = studentRepository.save(student);

        deleteResumeFile(previousResumePath);
        return convertToStudentProfileDTO(savedStudent);
    }

    public StudentProfileDTO updateStudentMarksheet(String email, MultipartFile marksheet) throws Exception {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        String previousMarksheetPath = student.getMarksheetUrl();
        String newMarksheetPath = storeStudentDocumentFile(marksheet, MARKSHEET_UPLOAD_DIR, "Marksheet");

        student.setMarksheetUrl(newMarksheetPath);
        Student savedStudent = studentRepository.save(student);

        deleteResumeFile(previousMarksheetPath);
        return convertToStudentProfileDTO(savedStudent);
    }

    public StudentProfileDTO deleteStudentMarksheet(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        String previousMarksheetPath = student.getMarksheetUrl();
        student.setMarksheetUrl(null);
        Student savedStudent = studentRepository.save(student);

        deleteResumeFile(previousMarksheetPath);
        return convertToStudentProfileDTO(savedStudent);
    }

    public List<StudentSemesterRecordDTO> getStudentSemesterRecordsByEmail(String email) {
        Student student = getStudentByEmail(email);
        return getStudentSemesterRecords(student.getId());
    }

    public List<StudentSemesterRecordDTO> getStudentSemesterRecords(Long studentId) {
        return studentSemesterRecordRepository.findByStudentIdOrderByCreatedAtAsc(studentId).stream()
                .map(this::convertToSemesterRecordDTO)
                .collect(Collectors.toList());
    }

    public StudentSemesterRecordDTO createStudentSemesterRecord(
            String email,
            String semesterName,
            Double percentage,
            MultipartFile marksheet
    ) throws Exception {
        Student student = getStudentByEmail(email);

        String trimmedSemesterName = semesterName == null ? "" : semesterName.trim();
        if (trimmedSemesterName.isBlank()) {
            throw new FieldValidationException("semesterName", "Semester name is required.");
        }

        if (trimmedSemesterName.length() > 100) {
            throw new FieldValidationException("semesterName", "Semester name must not exceed 100 characters.");
        }

        if (percentage != null && (percentage < 0 || percentage > 100)) {
            throw new FieldValidationException("percentage", "Percentage must be between 0 and 100.");
        }

        StudentSemesterRecord record = new StudentSemesterRecord();
        record.setStudent(student);
        record.setSemesterName(trimmedSemesterName);
        record.setPercentage(percentage);
        if (marksheet != null && !marksheet.isEmpty()) {
            record.setMarksheetUrl(storeStudentDocumentFile(marksheet, MARKSHEET_UPLOAD_DIR, "Marksheet"));
        }

        return convertToSemesterRecordDTO(studentSemesterRecordRepository.save(record));
    }

    public void deleteStudentSemesterRecord(String email, Long recordId) {
        Student student = getStudentByEmail(email);
        StudentSemesterRecord record = studentSemesterRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Semester record not found with id: " + recordId));

        if (!record.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("You can delete only your own semester records.");
        }

        deleteResumeFile(record.getMarksheetUrl());
        studentSemesterRecordRepository.delete(record);
    }

    public StudentProfileDTO deleteStudentResume(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        String previousResumePath = student.getResumeUrl();
        student.setResumeUrl(null);
        Student savedStudent = studentRepository.save(student);

        deleteResumeFile(previousResumePath);
        return convertToStudentProfileDTO(savedStudent);
    }

    public void deleteStudentApplications(Long studentId) {
        studentJobApplicationRepository.deleteByStudentId(studentId);
    }

    private String normalizePhoneNumber(String phone) {
        String digitsOnly = phone == null ? "" : phone.replaceAll("\\D", "");

        if (digitsOnly.startsWith("91") && digitsOnly.length() == 12) {
            return digitsOnly.substring(2);
        }

        return digitsOnly;
    }

    private StudentProfileDTO convertToStudentProfileDTO(Student student) {
        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setId(student.getId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setRollNumber(student.getRollNumber());
        dto.setDepartment(student.getDepartment());
        dto.setYear(student.getYear());
        dto.setCgpa(student.getCgpa());
        dto.setResumeUrl(student.getResumeUrl());
        if (student.getResumeUrl() != null) {
            dto.setResumeName(Path.of(student.getResumeUrl()).getFileName().toString());
        }
        dto.setSemesterName(student.getSemesterName());
        dto.setSemesterPercentage(student.getSemesterPercentage());
        dto.setMarksheetUrl(student.getMarksheetUrl());
        if (student.getMarksheetUrl() != null) {
            dto.setMarksheetName(Path.of(student.getMarksheetUrl()).getFileName().toString());
        }
        dto.setPlacementStatus(student.getPlacementStatus());
        return dto;
    }

    private String storeStudentDocumentFile(MultipartFile file, String uploadDir, String documentLabel) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(documentLabel + " file is required");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException(documentLabel + " must be a PDF file");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + originalFilename;
        Path destinationPath = Paths.get(dir.getAbsolutePath(), fileName);

        try {
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new Exception(documentLabel + " upload failed: " + e.getMessage());
        }

        return uploadDir + fileName;
    }

    private void deleteResumeFile(String resumePath) {
        if (resumePath == null || resumePath.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(Paths.get(resumePath));
        } catch (IOException ignored) {
        }
    }

    private CompanyOpportunityDTO convertToCompanyOpportunityDTO(Company company) {
        CompanyOpportunityDTO dto = new CompanyOpportunityDTO();
        dto.setId(company.getId());
        dto.setCompanyName(company.getCompanyName());
        dto.setIndustry(company.getIndustry());
        dto.setContactPerson(company.getContactPerson());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setWebsite(company.getWebsite());
        dto.setPositions(company.getPositions());
        dto.setSalaryPackage(company.getSalaryPackage());
        dto.setLocation(company.getLocation());
        dto.setJobDescription(company.getJobDescription());
        dto.setRegistrationStatus(company.getRegistrationStatus());
        return dto;
    }

    private StudentAppliedJobDTO convertToStudentAppliedJobDTO(StudentJobApplication application) {
        JobPosting jobPosting = application.getJobPosting();
        StudentAppliedJobDTO dto = new StudentAppliedJobDTO();
        dto.setApplicationId(application.getId());
        dto.setJobId(jobPosting.getId());
        dto.setCompanyId(jobPosting.getCompany().getId());
        dto.setCompanyName(jobPosting.getCompany().getCompanyName());
        dto.setTitle(jobPosting.getTitle());
        dto.setDescription(jobPosting.getDescription());
        dto.setLocation(jobPosting.getLocation());
        dto.setApplicationLink(jobPosting.getApplicationLink());
        dto.setJdFileUrl(jobPosting.getJdFileUrl());
        dto.setAppliedAt(application.getAppliedAt());
        return dto;
    }

    private StudentSemesterRecordDTO convertToSemesterRecordDTO(StudentSemesterRecord record) {
        StudentSemesterRecordDTO dto = new StudentSemesterRecordDTO();
        dto.setId(record.getId());
        dto.setSemesterName(record.getSemesterName());
        dto.setPercentage(record.getPercentage());
        dto.setMarksheetUrl(record.getMarksheetUrl());
        if (record.getMarksheetUrl() != null) {
            dto.setMarksheetName(Path.of(record.getMarksheetUrl()).getFileName().toString());
        }
        return dto;
    }
}
