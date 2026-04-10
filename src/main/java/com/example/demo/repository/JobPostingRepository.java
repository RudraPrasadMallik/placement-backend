package com.example.demo.repository;

import com.example.demo.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findAllByOrderByCreatedAtDesc();
    List<JobPosting> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<JobPosting> findByCompanyRegistrationStatusIgnoreCaseAndStatusIgnoreCaseOrderByCreatedAtDesc(
        String companyRegistrationStatus,
        String status
    );
    long countByCompanyId(Long companyId);
    void deleteByCompanyId(Long companyId);
}
