package com.joji.demo.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbHealthController {
    private final JdbcTemplate jdbc;

    public DbHealthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/db/health")
    public String health() {
        return jdbc.queryForObject(
                "SELECT note FROM health_check ORDER BY id DESC LIMIT 1",
                String.class
        );
    }
}
