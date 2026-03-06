package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.model.Admin;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        
        try {
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("User not found with email: " + request.getEmail());
                return response;
            }
            
            User user = userOptional.get();
            
            // Check password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                response.setSuccess(false);
                response.setMessage("Invalid password");
                return response;
            }
            
            // Check if user is active
            if (!user.isActive()) {
                response.setSuccess(false);
                response.setMessage("Account is deactivated. Contact admin.");
                return response;
            }
            
            // Set common response fields
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole().toString());
            response.setUserId(user.getId());
            response.setToken("dummy-jwt-token-" + System.currentTimeMillis()); // Simple token for now
            
            // If student, get additional details
            if (user.getRole().toString().equals("STUDENT")) {
                Optional<Student> studentOpt = studentRepository.findById(user.getId());
                if (studentOpt.isPresent()) {
                    // Any student specific data can be added here
                }
            }
            
            // If admin, get additional details
            if (user.getRole().toString().equals("ADMIN")) {
                Optional<Admin> adminOpt = adminRepository.findById(user.getId());
                if (adminOpt.isPresent()) {
                    // Any admin specific data can be added here
                }
            }
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Login failed: " + e.getMessage());
        }
        
        return response;
    }
    
    // Method to create default admin (call this once)
    public void createDefaultAdmin() {
        if (!userRepository.existsByEmail("admin@placement.com")) {
            Admin admin = new Admin();
            admin.setEmail("admin@placement.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setPhoneNumber("1234567890");
            admin.setRole(com.example.demo.model.Role.ADMIN);
            admin.setDepartment("Placement Cell");
            admin.setEmployeeId("ADMIN001");
            admin.setActive(true);
            
            adminRepository.save(admin);
            System.out.println("Default admin created - Email: admin@placement.com, Password: admin123");
        }
    }
}