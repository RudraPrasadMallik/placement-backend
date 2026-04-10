package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.config.JwtService;
import com.example.demo.model.Admin;
import com.example.demo.model.Company;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CompanyRepository;
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
    private AdminRepository adminRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtService jwtService;
    
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
                if (user.getRole().toString().equals("COMPANY")) {
                    response.setSuccess(false);
                    response.setMessage("Company account is waiting for admin approval.");
                    return response;
                }
                response.setSuccess(false);
                response.setMessage("Account is deactivated. Contact admin.");
                return response;
            }

            if (user.getRole().toString().equals("COMPANY")) {
                Company company = companyRepository.findByEmail(user.getEmail()).orElse(null);
                if (company != null && !"APPROVED".equalsIgnoreCase(company.getRegistrationStatus())) {
                    response.setSuccess(false);
                    response.setMessage("Company account is waiting for admin approval.");
                    return response;
                }
            }
            
            // Set common response fields
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole().toString());
            response.setUserId(user.getId());
            response.setToken(jwtService.generateToken(user));
            response.setTokenType("Bearer");
            response.setExpiresAt(System.currentTimeMillis() + jwtService.getExpirationMs());
            
            // If student, get additional details
            if (user.getRole().toString().equals("STUDENT")) {
                response.setRedirectPath("/student-dashboard");
                response.setStudentDashboard(studentService.getStudentDashboard(user.getId()));
            }

            if (user.getRole().toString().equals("COMPANY")) {
                response.setRedirectPath("/company-dashboard");
            }
            
            // If admin, get additional details
            if (user.getRole().toString().equals("ADMIN")) {
                response.setRedirectPath("/admin-dashboard");
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
            admin.setUsername("admin@placement.com");
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
