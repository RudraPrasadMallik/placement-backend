package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Student> findByEmail(String email);
}
