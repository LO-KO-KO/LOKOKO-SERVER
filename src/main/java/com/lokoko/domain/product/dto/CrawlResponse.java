package com.lokoko.domain.product.dto;

import java.util.List;

public record CrawlResponse(
        List<String> videoUrls
) {
    public static CrawlResponse of(List<String> videoUrls) {
        return new CrawlResponse(videoUrls);
    }
}
