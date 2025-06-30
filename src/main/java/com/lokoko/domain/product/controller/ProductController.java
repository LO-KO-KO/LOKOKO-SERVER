package com.lokoko.domain.product.controller;


import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_SEARCH_SUCCESS;

import com.lokoko.domain.product.controller.enums.ResponseMessage;
import com.lokoko.domain.product.dto.CategoryProductResponse;
import com.lokoko.domain.product.dto.CrawlRequest;
import com.lokoko.domain.product.service.ProductCrawlingService;
import com.lokoko.domain.product.service.ProductService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductCrawlingService productCrawlingService;

    @Hidden
    @PostMapping("/crawl")
    public ApiResponse<Void> crawl(@RequestBody CrawlRequest request) {
        productCrawlingService.scrapeByCategory(request.mainCategory(), request.middleCategory());

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CRAWL_SUCCESS.getMessage(), null);

    }

    // 카테고리별 제품 검색
    // 매개변수인 subCategoryId 는 서브 카테고리의 "ctgrNo" 입니다.
    // MainCategory, MiddleCategory 를 굳이 파라미터로 받지 않은 이유는,
    // SubCategory 만 있어도, 이에 대한 상위 계층인 MiddleCategory , MainCategory 모두 접근할 수 있기 때문입니다.
    @GetMapping("/categories/search")
    public ApiResponse<CategoryProductResponse> searchProductsByCategory(@RequestParam String subCategoryId) {

        CategoryProductResponse categoryProductResponse = productService.searchProductsByCategory(subCategoryId);

        return ApiResponse.success(HttpStatus.OK, CATEGORY_SEARCH_SUCCESS.getMessage(),
                categoryProductResponse);

    }
}
