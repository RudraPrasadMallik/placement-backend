package com.example.demo.controller;

import com.example.demo.dto.StudentRegistrationDTO;
import com.example.demo.dto.StudentDashboardDTO;
import com.example.demo.dto.StudentAppliedJobDTO;
import com.example.demo.dto.StudentProfileDTO;
import com.example.demo.dto.StudentProfileUpdateDTO;
import com.example.demo.dto.StudentRegistrationOptionsDTO;
import com.example.demo.dto.StudentSemesterRecordDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import com.example.demo.service.RegistrationOptionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        },
        allowCredentials = "true"
)
@Validated
@Tag(name = "Student Management", description = "Endpoints for managing student registrations")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private RegistrationOptionService registrationOptionService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerStudent(
            @RequestParam("fullName") @Valid String fullName,
            @RequestParam("email") @Valid String email,
            @RequestParam("phone") @Valid String phone,
            @RequestParam("rollNumber") @Valid String rollNumber,
            @RequestParam("department") @Valid String department,
            @RequestParam("year") @Valid String year,
            @RequestParam("cgpa") @Valid Double cgpa,
            @RequestParam("password") String password,
            @RequestParam("resume") MultipartFile resume
    ) throws Exception {
        // Additional file validation
        if (resume == null || resume.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Resume file is required");
            response.put("field", "resume");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Check file type
        String contentType = resume.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Only PDF files are allowed for resume");
            response.put("field", "resume");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Check file size (5MB limit)
        if (resume.getSize() > 5 * 1024 * 1024) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Resume file size must not exceed 5MB");
            response.put("field", "resume");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        StudentRegistrationDTO dto = new StudentRegistrationDTO();
        dto.setFullName(fullName);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setRollNumber(rollNumber);
        dto.setDepartment(department);
        dto.setYear(year);
        dto.setCgpa(cgpa);
        dto.setPassword(password);

        Set<ConstraintViolation<StudentRegistrationDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> fieldErrors = new HashMap<>();

            for (ConstraintViolation<StudentRegistrationDTO> violation : violations) {
                String fieldName = violation.getPropertyPath().toString();
                fieldErrors.putIfAbsent(fieldName, violation.getMessage());
            }

            response.put("status", "error");
            response.put("message", "Validation failed");
            response.put("fieldErrors", fieldErrors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Student student = studentService.registerStudent(dto, resume);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration submitted successfully!");
        response.put("rollNumber", student.getRollNumber());
        response.put("status", "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{studentId}/dashboard")
    public ResponseEntity<?> getStudentDashboard(@PathVariable Long studentId, Authentication authentication) {
        ResponseEntity<Map<String, String>> authError = validateStudentAccess(authentication, studentId);
        if (authError != null) {
            return authError;
        }

        StudentDashboardDTO dashboard = studentService.getStudentDashboard(studentId);
        if (dashboard.getStudent() != null) {
            dashboard.getStudent().setResumeUrl(toPublicResumeUrl(dashboard.getStudent().getResumeUrl()));
            dashboard.getStudent().setMarksheetUrl(toPublicResumeUrl(dashboard.getStudent().getMarksheetUrl()));
        }
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getLoggedInStudentProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to access the student profile.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can access this profile."));
        }

        StudentProfileDTO profile = studentService.getStudentProfileByEmail(authentication.getName());
        profile.setResumeUrl(toPublicResumeUrl(profile.getResumeUrl()));
        profile.setMarksheetUrl(toPublicResumeUrl(profile.getMarksheetUrl()));
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/registration-options")
    public ResponseEntity<StudentRegistrationOptionsDTO> getStudentRegistrationOptions() {
        return ResponseEntity.ok(registrationOptionService.getStudentRegistrationOptions());
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getAppliedJobs(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to view applied jobs.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can view applied jobs."));
        }

        List<StudentAppliedJobDTO> appliedJobs = studentService.getAppliedJobsByEmail(authentication.getName());
        return ResponseEntity.ok(appliedJobs);
    }

    @GetMapping("/profile/semesters")
    public ResponseEntity<?> getLoggedInStudentSemesters(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to view semester records.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can view semester records."));
        }

        List<StudentSemesterRecordDTO> records = studentService.getStudentSemesterRecordsByEmail(authentication.getName());
        records.forEach(record -> record.setMarksheetUrl(toPublicResumeUrl(record.getMarksheetUrl())));
        return ResponseEntity.ok(records);
    }

    @PostMapping(value = "/profile/semesters", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLoggedInStudentSemester(
            Authentication authentication,
            @RequestParam("semesterName") String semesterName,
            @RequestParam(value = "percentage", required = false) Double percentage,
            @RequestParam(value = "marksheet", required = false) MultipartFile marksheet
    ) throws Exception {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to add a semester record.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can add semester records."));
        }

        if (marksheet != null && !marksheet.isEmpty()) {
            ResponseEntity<Map<String, String>> validationError = validatePdfFile(marksheet, "marksheet", "Marksheet");
            if (validationError != null) {
                return validationError;
            }
        }

        StudentSemesterRecordDTO record = studentService.createStudentSemesterRecord(
                authentication.getName(),
                semesterName,
                percentage,
                marksheet
        );
        record.setMarksheetUrl(toPublicResumeUrl(record.getMarksheetUrl()));
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @DeleteMapping("/profile/semesters/{recordId}")
    public ResponseEntity<?> deleteLoggedInStudentSemester(
            Authentication authentication,
            @PathVariable Long recordId
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to delete a semester record.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can delete semester records."));
        }

        studentService.deleteStudentSemesterRecord(authentication.getName(), recordId);
        return ResponseEntity.ok(Map.of("message", "Semester record deleted successfully."));
    }

    @PostMapping("/applications/{jobId}")
    public ResponseEntity<?> applyForJob(@PathVariable Long jobId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to apply for jobs.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can apply for jobs."));
        }

        StudentAppliedJobDTO appliedJob = studentService.applyForJob(authentication.getName(), jobId);
        return ResponseEntity.ok(appliedJob);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateLoggedInStudentProfile(
            Authentication authentication,
            @Valid @RequestBody StudentProfileUpdateDTO dto
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to update the student profile.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can update this profile."));
        }

        StudentProfileDTO profile = studentService.updateStudentProfile(authentication.getName(), dto);
        profile.setResumeUrl(toPublicResumeUrl(profile.getResumeUrl()));
        profile.setMarksheetUrl(toPublicResumeUrl(profile.getMarksheetUrl()));
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/profile/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLoggedInStudentResume(
            Authentication authentication,
            @RequestParam("resume") MultipartFile resume
    ) throws Exception {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to update the student resume.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can update this resume."));
        }

        ResponseEntity<Map<String, String>> validationError = validateResumeFile(resume);
        if (validationError != null) {
            return validationError;
        }

        StudentProfileDTO profile = studentService.updateStudentResume(authentication.getName(), resume);
        profile.setResumeUrl(toPublicResumeUrl(profile.getResumeUrl()));
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/profile/resume")
    public ResponseEntity<?> deleteLoggedInStudentResume(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to delete the student resume.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can delete this resume."));
        }

        StudentProfileDTO profile = studentService.deleteStudentResume(authentication.getName());
        profile.setResumeUrl(toPublicResumeUrl(profile.getResumeUrl()));
        profile.setMarksheetUrl(toPublicResumeUrl(profile.getMarksheetUrl()));
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/profile/marksheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLoggedInStudentMarksheet(
            Authentication authentication,
            @RequestParam("marksheet") MultipartFile marksheet
    ) throws Exception {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to update the student marksheet.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can update this marksheet."));
        }

        ResponseEntity<Map<String, String>> validationError = validatePdfFile(marksheet, "marksheet", "Marksheet");
        if (validationError != null) {
            return validationError;
        }

        StudentProfileDTO profile = studentService.updateStudentMarksheet(authentication.getName(), marksheet);
        profile.setResumeUrl(toPublicResumeUrl(profile.getResumeUrl()));
        profile.setMarksheetUrl(toPublicResumeUrl(profile.getMarksheetUrl()));
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/profile/marksheet")
    public ResponseEntity<?> deleteLoggedInStudentMarksheet(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to delete the student marksheet.");
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only student accounts can delete this marksheet."));
        }

        StudentProfileDTO profile = studentService.deleteStudentMarksheet(authentication.getName());
        profile.setResumeUrl(toPublicResumeUrl(profile.getResumeUrl()));
        profile.setMarksheetUrl(toPublicResumeUrl(profile.getMarksheetUrl()));
        return ResponseEntity.ok(profile);
    }

    private ResponseEntity<Map<String, String>> validateStudentAccess(Authentication authentication, Long requestedStudentId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return unauthorizedResponse("Please log in to access student details.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (isAdmin) {
            return null;
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        if (!isStudent) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You are not allowed to access student details."));
        }

        Student student = studentService.getStudentByEmail(authentication.getName());
        if (!student.getId().equals(requestedStudentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You can access only your own student details."));
        }

        return null;
    }

    private ResponseEntity<Map<String, String>> unauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", message));
    }

    private String toPublicResumeUrl(String resumePath) {
        if (resumePath == null || resumePath.isBlank()) {
            return resumePath;
        }

        String normalizedPath = resumePath.startsWith("/") ? resumePath : "/" + resumePath;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(normalizedPath)
                .toUriString();
    }

    private ResponseEntity<Map<String, String>> validateResumeFile(MultipartFile resume) {
        return validatePdfFile(resume, "resume", "Resume");
    }

    private ResponseEntity<Map<String, String>> validatePdfFile(MultipartFile file, String fieldName, String label) {
        if (file == null || file.isEmpty()) {
            return badFileResponse(fieldName, label + " file is required");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        boolean isPdfContentType = "application/pdf".equalsIgnoreCase(contentType);
        boolean hasPdfExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf");

        if (!isPdfContentType && !hasPdfExtension) {
            return badFileResponse(fieldName, "Only PDF files are allowed for " + fieldName);
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return badFileResponse(fieldName, label + " file size must not exceed 5MB");
        }

        return null;
    }

    private ResponseEntity<Map<String, String>> badFileResponse(String fieldName, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("field", fieldName);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
