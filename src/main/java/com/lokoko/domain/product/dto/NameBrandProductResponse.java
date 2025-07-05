package com.lokoko.domain.product.dto;

import java.util.List;

public record NameBrandProductResponse(
        String searchQuery,
        int resultCount,
        List<ProductResponse> products
) {
}
