package com.example.demo.controller;

import com.example.demo.dto.ContactMessageDTO;
import com.example.demo.model.ContactMessage;
import com.example.demo.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitMessage(@RequestBody ContactMessageDTO dto) {
        try {
            ContactMessage message = contactService.saveMessage(dto);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message sent successfully! We'll get back to you soon.");
            response.put("status", "success");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to send message: " + e.getMessage());
            response.put("status", "error");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}