package com.example.demo.controller;

import com.example.demo.dto.AdminDashboardDTO;
import com.example.demo.dto.AdminStudentDTO;
import com.example.demo.dto.AdminCompanyDTO;
import com.example.demo.dto.AdminJobPostingDTO;
import com.example.demo.dto.CreateRegistrationOptionDTO;
import com.example.demo.dto.RegistrationOptionDTO;
import com.example.demo.dto.StudentSemesterRecordDTO;
import com.example.demo.model.RegistrationOptionType;
import com.example.demo.service.AdminService;
import com.example.demo.service.RegistrationOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin APIs", description = "APIs for admin dashboard and management")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        },
        allowCredentials = "true"
)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RegistrationOptionService registrationOptionService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboardData();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/students")
    @Operation(summary = "Get all registered students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminStudentDTO>> getAllStudents() {
        List<AdminStudentDTO> students = adminService.getAllStudents();
        students.forEach(this::applyStudentFileUrls);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/recent")
    @Operation(summary = "Get recent student registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminStudentDTO>> getRecentStudents(
            @RequestParam(defaultValue = "10") int limit) {
        List<AdminStudentDTO> students = adminService.getRecentStudents(limit);
        students.forEach(this::applyStudentFileUrls);
        return ResponseEntity.ok(students);
    }

    @PutMapping("/students/{studentId}/status")
    @Operation(summary = "Activate or deactivate student account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStudentDTO> toggleStudentStatus(
            @PathVariable Long studentId,
            @RequestParam boolean active) {
        AdminStudentDTO student = adminService.toggleStudentStatus(studentId, active);
        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/students/{studentId}")
    @Operation(summary = "Delete student account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        adminService.deleteStudent(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/companies")
    @Operation(summary = "Get all registered companies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminCompanyDTO>> getAllCompanies() {
        List<AdminCompanyDTO> companies = adminService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/companies/recent")
    @Operation(summary = "Get recent company registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminCompanyDTO>> getRecentCompanies(
            @RequestParam(defaultValue = "10") int limit) {
        List<AdminCompanyDTO> companies = adminService.getRecentCompanies(limit);
        return ResponseEntity.ok(companies);
    }

    @PutMapping("/companies/{companyId}/status")
    @Operation(summary = "Approve or reject company account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminCompanyDTO> toggleCompanyStatus(
            @PathVariable Long companyId,
            @RequestParam boolean active) {
        AdminCompanyDTO company = adminService.toggleCompanyStatus(companyId, active);
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/companies/{companyId}")
    @Operation(summary = "Delete company account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        adminService.deleteCompany(companyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jobs")
    @Operation(summary = "Get all posted jobs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminJobPostingDTO>> getAllJobs() {
        return ResponseEntity.ok(adminService.getAllJobPostings());
    }

    @PutMapping("/jobs/{jobId}/status")
    @Operation(summary = "Approve or reject a company job posting")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminJobPostingDTO> toggleJobStatus(
        @PathVariable Long jobId,
        @RequestParam boolean active
    ) {
        return ResponseEntity.ok(adminService.toggleJobStatus(jobId, active));
    }

    @GetMapping("/registration-options/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegistrationOptionDTO>> getRegistrationOptions(
            @PathVariable RegistrationOptionType type
    ) {
        return ResponseEntity.ok(registrationOptionService.getOptionsByType(type));
    }

    @PostMapping("/registration-options/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationOptionDTO> createRegistrationOption(
            @PathVariable RegistrationOptionType type,
            @RequestBody CreateRegistrationOptionDTO dto
    ) {
        return ResponseEntity.ok(registrationOptionService.createOption(type, dto));
    }

    @DeleteMapping("/registration-options/{optionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRegistrationOption(@PathVariable Long optionId) {
        registrationOptionService.deleteOption(optionId);
        return ResponseEntity.ok().build();
    }

    private void applyStudentFileUrls(AdminStudentDTO student) {
        student.setResumeUrl(toPublicFileUrl(student.getResumeUrl()));
        student.setMarksheetUrl(toPublicFileUrl(student.getMarksheetUrl()));
        if (student.getSemesterRecords() != null) {
            student.getSemesterRecords().forEach(this::applySemesterFileUrls);
        }
    }

    private void applySemesterFileUrls(StudentSemesterRecordDTO record) {
        record.setMarksheetUrl(toPublicFileUrl(record.getMarksheetUrl()));
    }

    private String toPublicFileUrl(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return filePath;
        }

        String normalizedPath = filePath.startsWith("/") ? filePath : "/" + filePath;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(normalizedPath)
                .toUriString();
    }
}
