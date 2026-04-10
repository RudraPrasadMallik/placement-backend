package com.example.demo.repository;

import com.example.demo.model.StudentJobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentJobApplicationRepository extends JpaRepository<StudentJobApplication, Long> {
    List<StudentJobApplication> findByStudentIdOrderByAppliedAtDesc(Long studentId);
    Optional<StudentJobApplication> findByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);
    boolean existsByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);
    void deleteByStudentId(Long studentId);
    void deleteByJobPostingCompanyId(Long companyId);
}
