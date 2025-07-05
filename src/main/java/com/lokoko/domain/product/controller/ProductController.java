package com.lokoko.domain.product.controller;


import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_NEW_LIST_SUCCESS;
import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_SEARCH_SUCCESS;

import com.lokoko.domain.product.controller.enums.ResponseMessage;
import com.lokoko.domain.product.dto.CategoryNewProductResponse;
import com.lokoko.domain.product.dto.CategoryProductResponse;
import com.lokoko.domain.product.dto.CrawlRequest;
import com.lokoko.domain.product.service.NewProductCrawlingService;
import com.lokoko.domain.product.service.ProductCrawlingService;
import com.lokoko.domain.product.service.ProductReadService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PRODUCT")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductReadService productReadService;
    private final ProductCrawlingService productCrawlingService;
    private final NewProductCrawlingService newProductCrawlingService;

    @Hidden
    @Operation(summary = "카테고리별 상품 크롤링")
    @PostMapping("/crawl")
    public ApiResponse<Void> crawl(@RequestBody CrawlRequest request) {
        productCrawlingService.scrapeByCategory(request.mainCategory(), request.middleCategory());

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_CRAWL_SUCCESS.getMessage(), null);

    }

    @Operation(summary = "카테고리 별 상품 검색")
    @GetMapping("/categories/search")
    public ApiResponse<CategoryProductResponse> searchProductsByCategory(@RequestParam String middleCategoryId,
                                                                         @RequestParam(required = false) String subCategoryId) {
        CategoryProductResponse categoryProductResponse = productReadService.searchProductsByCategory(middleCategoryId,
                subCategoryId);

        return ApiResponse.success(HttpStatus.OK, CATEGORY_SEARCH_SUCCESS.getMessage(), categoryProductResponse);
    }

    @Operation(summary = "신상품 카테고리별 조회")
    @GetMapping("/categories/new")
    public ApiResponse<CategoryNewProductResponse> searchNewProductsByCategory(@RequestParam String middleCategoryId) {
        CategoryNewProductResponse categoryNewProductResponse = productReadService.searchNewProductsByCategory(
                middleCategoryId);

        return ApiResponse.success(HttpStatus.OK, CATEGORY_NEW_LIST_SUCCESS.getMessage(), categoryNewProductResponse);
    }

    @Hidden
    @Operation(summary = "카테고리별 신제품 크롤링")
    @PostMapping("/crawl/new")
    public ApiResponse<Void> crawlNew(@RequestBody CrawlRequest request) {
        newProductCrawlingService.scrapeNewByCategory(request.mainCategory(), request.middleCategory());
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_CRAWL_NEW_SUCCESS.getMessage(), null);
    }

    @Hidden
    @Operation(summary = "상품 옵션 크롤링")
    @PostMapping("/crawl/options")
    public ApiResponse<Void> crawlOptions() {
        productCrawlingService.crawlAllOptions();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_OPTION_SUCCESS.getMessage(), null);
    }
}
