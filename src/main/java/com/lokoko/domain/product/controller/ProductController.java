package com.lokoko.domain.product.controller;


import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_SEARCH_SUCCESS;

import com.lokoko.domain.product.controller.enums.ResponseMessage;
import com.lokoko.domain.product.dto.CategoryProductResponse;
import com.lokoko.domain.product.dto.CrawlRequest;
import com.lokoko.domain.product.service.NewProductCrawlingService;
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
    private final NewProductCrawlingService newProductCrawlingService;

    @Hidden
    @PostMapping("/crawl")
    public ApiResponse<Void> crawl(@RequestBody CrawlRequest request) {
        productCrawlingService.scrapeByCategory(request.mainCategory(), request.middleCategory());

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_CRAWL_SUCCESS.getMessage(), null);

    }

    @GetMapping("/categories/search")
    public ApiResponse<CategoryProductResponse> searchProductsByCategory(@RequestParam String middleCategoryId,
                                                                         @RequestParam(required = false) String subCategoryId) {

        CategoryProductResponse categoryProductResponse = productService.searchProductsByCategory(middleCategoryId,
                subCategoryId);

        return ApiResponse.success(HttpStatus.OK, CATEGORY_SEARCH_SUCCESS.getMessage(),
                categoryProductResponse);
    }

    @Hidden
    @PostMapping("/crawl/new")
    public ApiResponse<Void> crawlNew(@RequestBody CrawlRequest request) {
        newProductCrawlingService.scrapeNewByCategory(request.mainCategory(), request.middleCategory());
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_CRAWL_NEW_SUCCESS.getMessage(), null);
    }
}
