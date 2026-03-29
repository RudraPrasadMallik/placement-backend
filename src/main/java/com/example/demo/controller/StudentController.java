package com.example.demo.controller;

import com.example.demo.dto.StudentRegistrationDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
//@Tag(name = "Student Management", description = "Endpoints for managing student registrations")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerStudent(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("rollNumber") String rollNumber,
            @RequestParam("department") String department,
            @RequestParam("year") String year,
            @RequestParam("cgpa") Double cgpa,
            @RequestParam("resume") MultipartFile resume
    ) {
        try {
            StudentRegistrationDTO dto = new StudentRegistrationDTO();
            dto.setFullName(fullName);
            dto.setEmail(email);
            dto.setPhone(phone);
            dto.setRollNumber(rollNumber);
            dto.setDepartment(department);
            dto.setYear(year);
            dto.setCgpa(cgpa);

            Student student = studentService.registerStudent(dto, resume);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration submitted successfully!");
            response.put("rollNumber", student.getRollNumber());
            response.put("status", "success");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration failed: " + e.getMessage());
            response.put("status", "error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}