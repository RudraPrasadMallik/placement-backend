package com.example.demo.service;

import com.example.demo.dto.StudentRegistrationDTO;
import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Register new student
    public Student registerStudent(StudentRegistrationDTO dto) throws Exception {
        // Check if email already exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new Exception("Email already registered!");
        }
        
        Student student = new Student();
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setFullName(dto.getFullName());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setRole(Role.STUDENT);
        
        // Student specific fields
        student.setRegistrationNumber(dto.getRegistrationNumber());
        student.setDepartment(dto.getDepartment());
        student.setSemester(dto.getSemester());
        student.setCgpa(dto.getCgpa());
        student.setBacklogs(dto.getBacklogs());
        student.setTenthPercentage(dto.getTenthPercentage());
        student.setTwelfthPercentage(dto.getTwelfthPercentage());
        student.setGraduationPercentage(dto.getGraduationPercentage());
        student.setSkills(dto.getSkills());
        student.setAddress(dto.getAddress());
        student.setPlacementStatus("NOT_PLACED");
        
        return studentRepository.save(student);
    }
    
    // Get all students (for admin)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    // Get student by ID
    public Student getStudentById(Long id) throws Exception {
        return studentRepository.findById(id)
                .orElseThrow(() -> new Exception("Student not found with id: " + id));
    }
    
    // Update student profile
    public Student updateStudent(Long id, Student studentDetails) throws Exception {
        Student student = getStudentById(id);
        
        student.setFullName(studentDetails.getFullName());
        student.setPhoneNumber(studentDetails.getPhoneNumber());
        student.setSemester(studentDetails.getSemester());
        student.setCgpa(studentDetails.getCgpa());
        student.setBacklogs(studentDetails.getBacklogs());
        student.setSkills(studentDetails.getSkills());
        student.setAddress(studentDetails.getAddress());
        student.setResumeUrl(studentDetails.getResumeUrl());
        
        return studentRepository.save(student);
    }
    
    // Delete student
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    
    // Get students by department
    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }
    
    // Get students by placement status
    public List<Student> getStudentsByStatus(String status) {
        return studentRepository.findByPlacementStatus(status);
    }
    
    // Get eligible students for a job
    public List<Student> getEligibleStudents(Double minCGPA, Integer maxBacklogs) {
        return studentRepository.findEligibleStudents(minCGPA, maxBacklogs);
    }
}