package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;

public record CrawlRequest(
        MainCategory mainCategory,
        MiddleCategory middleCategory
) {
}
