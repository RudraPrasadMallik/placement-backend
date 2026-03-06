package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.StudentRegistrationDTO;
import com.example.demo.model.Student;
import com.example.demo.service.AuthService;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private StudentService studentService;
    
    // Student Registration
    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegistrationDTO registrationDTO) {
        try {
            Student student = studentService.registerStudent(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Student registered successfully! Registration number: " + student.getRegistrationNumber());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }
    
    // Login (for both Student and Admin)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Auth API is working!";
    }
}