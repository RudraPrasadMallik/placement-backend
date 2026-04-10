package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);

    @Modifying
    @Query(
        value = """
            INSERT INTO users (id, email, username, password, full_name, phone_number, role, active, created_at, updated_at)
            VALUES (:id, :email, :username, :password, :fullName, :phoneNumber, :role, :active, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """,
        nativeQuery = true
    )
    void insertLegacyCompanyUser(
        @Param("id") Long id,
        @Param("email") String email,
        @Param("username") String username,
        @Param("password") String password,
        @Param("fullName") String fullName,
        @Param("phoneNumber") String phoneNumber,
        @Param("role") String role,
        @Param("active") boolean active
    );
}
