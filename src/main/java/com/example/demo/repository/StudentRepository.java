package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByDepartment(String department);
    List<Student> findByPlacementStatus(String status);
    
    @Query("SELECT s FROM Student s WHERE s.cgpa >= :minCGPA AND s.backlogs <= :maxBacklogs")
    List<Student> findEligibleStudents(@Param("minCGPA") Double minCGPA, @Param("maxBacklogs") Integer maxBacklogs);
}