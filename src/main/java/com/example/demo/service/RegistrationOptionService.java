package com.example.demo.service;

import com.example.demo.dto.CreateRegistrationOptionDTO;
import com.example.demo.dto.RegistrationOptionDTO;
import com.example.demo.dto.StudentRegistrationOptionsDTO;
import com.example.demo.model.RegistrationOption;
import com.example.demo.model.RegistrationOptionType;
import com.example.demo.repository.RegistrationOptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationOptionService {

    private final RegistrationOptionRepository registrationOptionRepository;

    public RegistrationOptionService(RegistrationOptionRepository registrationOptionRepository) {
        this.registrationOptionRepository = registrationOptionRepository;
    }

    public StudentRegistrationOptionsDTO getStudentRegistrationOptions() {
        StudentRegistrationOptionsDTO dto = new StudentRegistrationOptionsDTO();
        dto.setDepartments(getOptionsByType(RegistrationOptionType.DEPARTMENT));
        dto.setYears(getOptionsByType(RegistrationOptionType.YEAR));
        return dto;
    }

    public List<RegistrationOptionDTO> getOptionsByType(RegistrationOptionType type) {
        return registrationOptionRepository.findByTypeOrderBySortOrderAscValueAsc(type)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public RegistrationOptionDTO createOption(RegistrationOptionType type, CreateRegistrationOptionDTO dto) {
        String trimmedValue = dto.getValue().trim();
        if (registrationOptionRepository.existsByTypeAndValueIgnoreCase(type, trimmedValue)) {
            throw new RuntimeException(type.name() + " option already exists: " + trimmedValue);
        }

        RegistrationOption option = new RegistrationOption();
        option.setType(type);
        option.setValue(trimmedValue);
        option.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : (int) registrationOptionRepository.countByType(type));

        return toDto(registrationOptionRepository.save(option));
    }

    public void deleteOption(Long optionId) {
        if (!registrationOptionRepository.existsById(optionId)) {
            throw new RuntimeException("Registration option not found with id: " + optionId);
        }
        registrationOptionRepository.deleteById(optionId);
    }

    public void ensureDefaults() {
        seedType(RegistrationOptionType.DEPARTMENT, List.of(
                "CSE",
                "ECE",
                "EEE",
                "ME",
                "CE"
        ));
        seedType(RegistrationOptionType.YEAR, List.of(
                "1st Year",
                "2nd Year",
                "3rd Year",
                "4th Year"
        ));
    }

    private void seedType(RegistrationOptionType type, List<String> values) {
        long existingCount = registrationOptionRepository.countByType(type);
        int sortOrder = (int) existingCount;

        for (int i = 0; i < values.size(); i++) {
            if (registrationOptionRepository.existsByTypeAndValueIgnoreCase(type, values.get(i))) {
                continue;
            }

            RegistrationOption option = new RegistrationOption();
            option.setType(type);
            option.setValue(values.get(i));
            option.setSortOrder(existingCount > 0 ? sortOrder++ : i);
            registrationOptionRepository.save(option);
        }
    }

    private RegistrationOptionDTO toDto(RegistrationOption option) {
        RegistrationOptionDTO dto = new RegistrationOptionDTO();
        dto.setId(option.getId());
        dto.setValue(option.getValue());
        dto.setSortOrder(option.getSortOrder());
        return dto;
    }
}
