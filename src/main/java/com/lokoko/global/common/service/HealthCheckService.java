package com.lokoko.global.common.service;

import com.lokoko.global.common.response.HealthSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    public HealthSummary getHealthSummary() {
        Runtime rt = Runtime.getRuntime();
        long free = rt.freeMemory();
        long total = rt.totalMemory();
        int threads = Thread.activeCount();
        return new HealthSummary(free, total, threads);
    }
}
