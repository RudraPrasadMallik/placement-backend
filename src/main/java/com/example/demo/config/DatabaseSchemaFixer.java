package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        Integer foreignKeyCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.TABLE_CONSTRAINTS
                WHERE CONSTRAINT_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'companies'
                  AND CONSTRAINT_NAME = 'FK75nyyir60ukpfq5v0v2jl0fri'
                  AND CONSTRAINT_TYPE = 'FOREIGN KEY'
                """,
                Integer.class
        );

        if (foreignKeyCount != null && foreignKeyCount > 0) {
            jdbcTemplate.execute("ALTER TABLE companies DROP FOREIGN KEY FK75nyyir60ukpfq5v0v2jl0fri");
        }
    }
}
