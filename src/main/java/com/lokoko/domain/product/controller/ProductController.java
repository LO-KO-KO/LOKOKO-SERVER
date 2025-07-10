package com.lokoko.domain.product.controller;


import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_NEW_LIST_SUCCESS;
import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_POPULAR_LIST_SUCCESS;
import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_REVIEW_SEARCH_SUCCESS;
import static com.lokoko.domain.product.controller.enums.ResponseMessage.CATEGORY_SEARCH_SUCCESS;
import static com.lokoko.domain.product.controller.enums.ResponseMessage.PRODUCT_DETAIL_SUCCESS;
import static com.lokoko.domain.product.controller.enums.ResponseMessage.PRODUCT_YOUTUBE_DETAIL_SUCCESS;

import com.lokoko.domain.product.controller.enums.ResponseMessage;
import com.lokoko.domain.product.dto.CategoryNewProductResponse;
import com.lokoko.domain.product.dto.CategoryPopularProductResponse;
import com.lokoko.domain.product.dto.CategoryProductPageResponse;
import com.lokoko.domain.product.dto.CrawlRequest;
import com.lokoko.domain.product.dto.NameBrandProductResponse;
import com.lokoko.domain.product.dto.ProductDetailResponse;
import com.lokoko.domain.product.dto.ProductDetailYoutubeResponse;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.service.NewProductCrawlingService;
import com.lokoko.domain.product.service.ProductCrawlingService;
import com.lokoko.domain.product.service.ProductReadService;
import com.lokoko.domain.product.service.ProductService;
import com.lokoko.domain.review.dto.ImageReviewListResponse;
import com.lokoko.domain.review.dto.KeywordImageReviewListResponse;
import com.lokoko.domain.review.dto.KeywordVideoReviewListResponse;
import com.lokoko.domain.review.dto.VideoReviewListResponse;
import com.lokoko.domain.review.exception.MissingMediaTypeException;
import com.lokoko.domain.review.service.ReviewReadService;
import com.lokoko.global.common.entity.MediaType;
import com.lokoko.global.common.entity.SearchType;
import com.lokoko.global.common.response.ApiResponse;
import com.lokoko.global.kuromoji.service.ProductMigrationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ProductService productService;
    private final ProductCrawlingService productCrawlingService;
    private final NewProductCrawlingService newProductCrawlingService;
    private final ProductMigrationService productMigrationService;
    private final ReviewReadService reviewReadService;

    @Hidden
    @Operation(summary = "카테고리별 상품 크롤링")
    @PostMapping("/crawl")
    public ApiResponse<Void> crawl(@RequestBody CrawlRequest request) {
        productCrawlingService.scrapeByCategory(request.mainCategory(), request.middleCategory());

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_CRAWL_SUCCESS.getMessage(), null);

    }

    @Operation(summary = "카테고리 별 상품 검색")
    @GetMapping("/categories/search")
    public ApiResponse<?> searchProductsByCategory(
            @RequestParam MiddleCategory middleCategory,
            @RequestParam(required = false) SubCategory subCategory,
            @RequestParam(defaultValue = "false") SearchType searchType,
            @RequestParam(required = false) MediaType mediaType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (searchType == SearchType.REVIEW) {
            if (mediaType == MediaType.VIDEO) {
                VideoReviewListResponse videoReviewResponse = reviewReadService.searchVideoReviewsByCategory(
                        middleCategory, subCategory, page, size);

                return ApiResponse.success(HttpStatus.OK, CATEGORY_REVIEW_SEARCH_SUCCESS.getMessage(),
                        videoReviewResponse);

            } else if (mediaType == MediaType.IMAGE) {
                ImageReviewListResponse imageReviewListResponse = reviewReadService.searchImageReviewsByCategory(
                        middleCategory, subCategory, page, size);

                return ApiResponse.success(HttpStatus.OK, CATEGORY_REVIEW_SEARCH_SUCCESS.getMessage(),
                        imageReviewListResponse);
            }
            throw new MissingMediaTypeException();
        }
        CategoryProductPageResponse categoryProductResponse = productReadService.searchProductsByCategory(
                middleCategory, subCategory, page, size);

        return ApiResponse.success(HttpStatus.OK, CATEGORY_SEARCH_SUCCESS.getMessage(), categoryProductResponse);
    }

    @Operation(summary = "상품명 또는 브랜드명 상품 및 리뷰 검색")
    @GetMapping("/search")
    public ApiResponse<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "false") SearchType searchType,
            @RequestParam(required = false) MediaType mediaType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // 리뷰 검색
        if (searchType == SearchType.REVIEW) {

            // 동영상 리뷰
            if (mediaType == MediaType.VIDEO) {
                KeywordVideoReviewListResponse videoReviewResponse = reviewReadService.searchVideoReviewsByKeyword(
                        keyword, page, size);

                return ApiResponse.success(HttpStatus.OK, ResponseMessage.NAME_BRAND_REVIEW_SEARCH_SUCCESS.getMessage(),
                        videoReviewResponse);
                // 이미지 리뷰
            } else if (mediaType == MediaType.IMAGE) {
                KeywordImageReviewListResponse imageReviewResponse = reviewReadService.searchImageReviewsByKeyword(
                        keyword, page, size);

                return ApiResponse.success(HttpStatus.OK, ResponseMessage.NAME_BRAND_REVIEW_SEARCH_SUCCESS.getMessage(),
                        imageReviewResponse);
            }

            throw new MissingMediaTypeException();
        }
        // 상품 검색
        NameBrandProductResponse searchResults = productService.search(keyword, page,
                size);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.NAME_BRAND_SEARCH_SUCCESS.getMessage(),
                searchResults);

    }

    @Operation(summary = "신상품 카테고리별 조회")
    @GetMapping("/categories/new")
    public ApiResponse<CategoryNewProductResponse> searchNewProductsByCategory(
            @RequestParam MiddleCategory middleCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        CategoryNewProductResponse categoryNewProductResponse = productReadService.searchNewProductsByCategory(
                middleCategory, page, size);

        return ApiResponse.success(HttpStatus.OK, CATEGORY_NEW_LIST_SUCCESS.getMessage(), categoryNewProductResponse);
    }

    @Operation(summary = "인기상품 카테고리별 조회")
    @GetMapping("/categories/popular")
    public ApiResponse<CategoryPopularProductResponse> searchPopularProductsByCategory(
            @RequestParam MiddleCategory middleCategory, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        CategoryPopularProductResponse categoryPopularProductResponse = productReadService.searchPopularProductsByCategory(
                middleCategory, page, size);

        return ApiResponse.success(HttpStatus.OK, CATEGORY_POPULAR_LIST_SUCCESS.getMessage(),
                categoryPopularProductResponse);
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

    @Operation(summary = "상세조회 제품(별점 포함) 조회")
    @GetMapping("/details/{productId}")
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
        ProductDetailResponse detail = productReadService.getProductDetail(productId);

        return ApiResponse.success(HttpStatus.OK, PRODUCT_DETAIL_SUCCESS.getMessage(), detail);
    }

    @Operation(summary = "상세조회 유튜브 리뷰 조회")
    @GetMapping("/details/{productId}/youtube")
    public ApiResponse<ProductDetailYoutubeResponse> getProductDetailYoutube(@PathVariable Long productId) {
        ProductDetailYoutubeResponse detailYoutube = productReadService.getProductDetailYoutube(productId);

        return ApiResponse.success(HttpStatus.OK, PRODUCT_YOUTUBE_DETAIL_SUCCESS.getMessage(), detailYoutube);
    }

    @Hidden
    @Operation(summary = "상품 엔티티의 search_token 필드 갱신")
    @PostMapping("/search-fields/migrate")
    public ApiResponse<String> updateSearchFields() {
        productMigrationService.migrateSearchFields();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PRODUCT_MIGRATION_SUCCESS.getMessage(), null);
    }
}
