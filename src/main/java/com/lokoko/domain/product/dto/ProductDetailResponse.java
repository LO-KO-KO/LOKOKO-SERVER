package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import java.util.List;

public record ProductDetailResponse(
        List<ProductResponse> products,
        List<ProductOptionResponse> productOptions,
        long normalPrice,
        String productDetail,
        String ingredients,
        String shippingInfo,
        String oliveYoungUrl,
        String q10Url,
        MiddleCategory middleCategory,
        SubCategory subCategory
) {
}
