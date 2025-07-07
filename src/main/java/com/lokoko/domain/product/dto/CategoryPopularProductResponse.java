package com.lokoko.domain.product.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

public record CategoryPopularProductResponse(
        String searchQuery,
        List<ProductResponse> products,
        PageableResponse pageInfo
) {
}
