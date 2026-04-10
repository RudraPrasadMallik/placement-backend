package com.example.demo.repository;

import com.example.demo.model.StudentSemesterRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSemesterRecordRepository extends JpaRepository<StudentSemesterRecord, Long> {
    List<StudentSemesterRecord> findByStudentIdOrderByCreatedAtAsc(Long studentId);
}
