package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import java.util.List;

public record ProductDetailResponse(
        List<ProductResponse> products,
        List<String> productOptions,
        List<String> imageUrls,
        String brandName,
        long normalPrice,
        String productDetail,
        String unit,
        String ingredients,
        String shippingInfo,
        String oliveYoungUrl,
        String q10Url,
        MiddleCategory middleCategory,
        SubCategory subCategory
) {
}
