package com.lokoko.global.config;

import org.springframework.stereotype.Component;

@Component
public class PermitUrlConfig {

    public String[] getPublicUrl() {
        return new String[]{
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/auth/**",
        };
    }

    public String[] getUserUrl() {
        return new String[]{
                "/api/youtubes/**"
        };
    }

    public String[] getAdminUrl() {
        return new String[]{
                "/api/admin/**",
                "/api/products/crawl",
        };
    }

}
