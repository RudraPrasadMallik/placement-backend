package com.example.demo.config;

import com.example.demo.model.Admin;
import com.example.demo.model.Role;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RegistrationOptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationOptionService registrationOptionService;

    @Value("${app.registration-options.seed-defaults:false}")
    private boolean seedRegistrationDefaults;
    
    @Override
    public void run(String... args) throws Exception {
        if (seedRegistrationDefaults) {
            registrationOptionService.ensureDefaults();
        }

        // Create default admin if not exists
        if (!userRepository.existsByEmail("admin@placement.com")) {
            Admin admin = new Admin();
            admin.setEmail("admin@placement.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setPhoneNumber("1234567890");
            admin.setRole(Role.ADMIN);
            admin.setDepartment("Placement Cell");
            admin.setEmployeeId("ADMIN001");
            admin.setActive(true);
            
            adminRepository.save(admin);
            System.out.println("======================================");
            System.out.println("Default Admin Created:");
            System.out.println("Email: admin@placement.com");
            System.out.println("Password: admin123");
            System.out.println("======================================");
        }
    }
}
