package com.lokoko.domain.product.dto;

import java.util.List;

public record CategoryNewProductResponse(
        String middleCategory,
        List<ProductResponse> products
) {
}
