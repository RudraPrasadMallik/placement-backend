package com.example.demo.service;

import com.example.demo.dto.StudentRegistrationDTO;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student registerStudent(StudentRegistrationDTO dto, MultipartFile resume) throws Exception {

        // Check if email already exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new Exception("Email already registered!");
        }

        // Handle file upload
        String uploadDir = "uploads/resumes/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + resume.getOriginalFilename();
        String filePath = uploadDir + fileName;

        try {
            resume.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new Exception("Resume upload failed: " + e.getMessage());
        }

        // Create student with user details
        Student student = new Student();
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getRollNumber())); // Default password = roll number
        student.setFullName(dto.getFullName());
        student.setPhoneNumber(dto.getPhone());
        student.setActive(true);
        
        // Set student-specific fields
        student.setRollNumber(dto.getRollNumber());
        student.setDepartment(dto.getDepartment());
        student.setYear(dto.getYear());
        student.setCgpa(dto.getCgpa());
        student.setResumeUrl(filePath);
        student.setPlacementStatus("NOT_PLACED");

        return studentRepository.save(student);
    }
}