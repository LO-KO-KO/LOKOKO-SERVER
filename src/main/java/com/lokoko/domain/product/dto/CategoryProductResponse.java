package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.global.common.entity.dto.PageableResponse;
import java.util.List;
import org.springframework.data.domain.Slice;

// 카테고리 검색의 결과를 포함하고 있는 DTO 입니다.
public record CategoryProductResponse(
        // 검색창에는 사용자가 검색한 서브 카테고리 이름
        String searchQuery,
        // 사용자가 선택한 MainCategory
        String mainCategory,
        // 사용자가 선택한 SubCategory
        String subCategory,
        // 검색 결과 제품 리스트
        List<ProductResponse> products,

        PageableResponse pageable
) {

    public static CategoryProductResponse of(
            String searchQuery,
            String mainCategory,
            String subCategory,
            Slice<Product> productSlice,
            List<ProductResponse> products
    ) {
        PageableResponse pageable = PageableResponse.from(productSlice);

        return new CategoryProductResponse(
                searchQuery,
                mainCategory,
                subCategory,
                products,
                pageable
        );


    }
}

