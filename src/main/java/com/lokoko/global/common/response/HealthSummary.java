package com.lokoko.global.common.response;

public record HealthSummary(
        long freeMemory,
        long totalMemory,
        int threadCount
) {
}
