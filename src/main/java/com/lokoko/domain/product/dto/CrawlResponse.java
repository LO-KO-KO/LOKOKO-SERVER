package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;

public record CrawlResponse(
        String message
) {
    public static CrawlResponse of(MainCategory main, MiddleCategory middle) {
        return new CrawlResponse(
                String.format("%s / %s", main, middle)
        );
    }
}
