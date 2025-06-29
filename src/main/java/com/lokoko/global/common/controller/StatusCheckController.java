package com.lokoko.global.common.controller;

import com.lokoko.global.common.response.HealthSummary;
import com.lokoko.global.common.service.HealthCheckService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
public class StatusCheckController {
    private final HealthCheckService healthCheckService;

    @GetMapping("/health-check")
    public ResponseEntity<HealthSummary> checkHealthStatus() {
        return ResponseEntity.ok(healthCheckService.getHealthSummary());
    }
}
