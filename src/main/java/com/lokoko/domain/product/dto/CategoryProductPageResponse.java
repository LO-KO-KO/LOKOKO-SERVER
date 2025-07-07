package com.lokoko.domain.product.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record CategoryProductPageResponse(
        String searchQuery,
        String parentCategoryName,
        List<ProductResponse> products,
        PageableResponse pageInfo
) {
}
