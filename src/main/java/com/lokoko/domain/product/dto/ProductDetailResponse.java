package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import java.util.List;

public record ProductDetailResponse(

        Long productId, // 제품 id(추후 상세조회를 위해서)
        List<String> imageUrls, // 제품 이미지
        String productName,// 제품 이름
        String brandName, // 브랜드 이름
        String unit, // 제품 단위
        Long reviewCount, // 리뷰 개수
        Double rating, // 별점
        List<String> productOptions,
        long normalPrice,
        String productDetail,
        String ingredients,
        String oliveYoungUrl,
        String q10Url,
        MiddleCategory middleCategory,
        SubCategory subCategory
) {
    public static ProductDetailResponse from(ProductResponse response, List<String> productOptions, Product product) {

        return new ProductDetailResponse(
                response.productId(),
                response.imageUrls(),
                response.productName(),
                response.brandName(),
                response.unit(),
                response.reviewCount(),
                response.rating(),
                productOptions,
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
