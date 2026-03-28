package com.example.demo.controller;

import com.example.demo.dto.CompanyRegistrationDTO;
import com.example.demo.model.Company;
import com.example.demo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@RequestBody CompanyRegistrationDTO dto) {
        try {
            Company company = companyService.registerCompany(dto);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration submitted successfully! We'll contact you soon.");
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