package com.lokoko.domain.product.dto.response;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import java.util.List;

public record ProductDetailResponse(

        Long productId,
        List<String> imageUrls,
        List<ProductOptionResponse> productOptions,
        String productName,
        String brandName,
        String unit,
        Long reviewCount,
        Double rating,
        long normalPrice,
        String productDetail,
        String ingredients,
        String oliveYoungUrl,
        String q10Url,
        MiddleCategory middleCategory,
        SubCategory subCategory
) {
    public static ProductDetailResponse from(ProductResponse response, List<ProductOptionResponse> productOptions, Product product) {

        return new ProductDetailResponse(
                response.productId(),
                response.imageUrls(),
                productOptions,
                response.productName(),
                response.brandName(),
                response.unit(),
                response.reviewCount(),
                response.rating(),
                product.getNormalPrice(),
                product.getProductDetail(),
                product.getIngredients(),
                product.getOliveYoungUrl(),
                product.getQoo10Url(),
                product.getMiddleCategory(),
                product.getSubCategory()
        );
    }
}
