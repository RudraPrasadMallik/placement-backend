package com.example.demo.repository;

import com.example.demo.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Company> findByRegistrationStatusIgnoreCase(String registrationStatus);

    @Query(
        value = """
            SELECT c.id
            FROM companies c
            LEFT JOIN users u ON u.id = c.id
            WHERE LOWER(c.email) = LOWER(:email) AND u.id IS NULL
            LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<Long> findLegacyCompanyIdByEmail(@Param("email") String email);

    @Modifying
    @Query(
        value = """
            UPDATE companies
            SET company_name = :companyName,
                industry = :industry,
                contact_person = :contactPerson,
                email = :email,
                phone = :phone,
                website = :website,
                positions = :positions,
                salary_package = :salaryPackage,
                location = :location,
                job_description = :jobDescription,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = :companyId
            """,
        nativeQuery = true
    )
    void updateLegacyCompanyRegistration(
        @Param("companyId") Long companyId,
        @Param("companyName") String companyName,
        @Param("industry") String industry,
        @Param("contactPerson") String contactPerson,
        @Param("email") String email,
        @Param("phone") String phone,
        @Param("website") String website,
        @Param("positions") Integer positions,
        @Param("salaryPackage") String salaryPackage,
        @Param("location") String location,
        @Param("jobDescription") String jobDescription
    );
}
