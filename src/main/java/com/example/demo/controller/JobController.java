package com.example.demo.controller;

import com.example.demo.dto.PublicCompanyJobsDTO;
import com.example.demo.service.JobPostingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        },
        allowCredentials = "true"
)
public class JobController {
    private final JobPostingService jobPostingService;

    public JobController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping("/companies")
    public ResponseEntity<List<PublicCompanyJobsDTO>> getApprovedCompanyJobs() {
        return ResponseEntity.ok(jobPostingService.getApprovedCompanyJobs());
    }
}
