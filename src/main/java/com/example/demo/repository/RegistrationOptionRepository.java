package com.example.demo.repository;

import com.example.demo.model.RegistrationOption;
import com.example.demo.model.RegistrationOptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationOptionRepository extends JpaRepository<RegistrationOption, Long> {
    List<RegistrationOption> findByTypeOrderBySortOrderAscValueAsc(RegistrationOptionType type);
    boolean existsByTypeAndValueIgnoreCase(RegistrationOptionType type, String value);
    long countByType(RegistrationOptionType type);
}
