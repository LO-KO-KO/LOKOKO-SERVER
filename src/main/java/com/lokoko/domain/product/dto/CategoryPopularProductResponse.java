package com.lokoko.domain.product.dto;

import java.util.List;

public record CategoryPopularProductResponse(
        String middleCategory,
        List<ProductResponse> products
) {
}
