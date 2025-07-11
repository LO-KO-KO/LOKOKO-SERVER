package com.lokoko.domain.product.service;


import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.dto.response.CategoryNewProductResponse;
import com.lokoko.domain.product.dto.response.CategoryPopularProductResponse;
import com.lokoko.domain.product.dto.response.CategoryProductPageResponse;
import com.lokoko.domain.product.dto.response.ProductDetailResponse;
import com.lokoko.domain.product.dto.response.ProductDetailYoutubeResponse;
import com.lokoko.domain.product.dto.response.ProductOptionResponse;
import com.lokoko.domain.product.dto.response.ProductResponse;
import com.lokoko.domain.product.dto.response.ProductSummary;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.ProductOption;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import com.lokoko.domain.product.exception.MiddleCategoryNotFoundException;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.exception.SubCategoryNotFoundException;
import com.lokoko.domain.product.repository.ProductOptionRepository;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductReadService {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ReviewRepository reviewRepository;

    // 카테고리 id 로 제품 리스트 조회
    public CategoryProductPageResponse searchProductsByCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                                Long userId, int page,
                                                                int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Product> slice = (subCategory == null)
                ? productRepository.findProductsByPopularityAndRating(middleCategory, pageable)
                : productRepository.findProductsByPopularityAndRating(middleCategory, subCategory, pageable);

        Slice<ProductResponse> responseSlice =
                productService.buildProductResponseWithReviewData(slice, userId);

        return CategoryProductPageResponse.builder()
                .searchQuery(subCategory == null
                        ? middleCategory.getDisplayName()
                        : subCategory.getDisplayName())
                .parentCategoryName(subCategory == null
                        ? middleCategory.getParent().getDisplayName()
                        : subCategory.getMiddleCategory().getParent().getDisplayName())
                .products(responseSlice.getContent())
                .pageInfo(PageableResponse.of(responseSlice))
                .build();
    }

    public CategoryNewProductResponse searchNewProductsByCategory(MiddleCategory middleCategory, Long userId, int page,
                                                                  int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Product> slice = productRepository.findByMiddleCategoryAndTag(
                middleCategory, Tag.NEW, pageable
        );
        Slice<ProductResponse> responseSlice =
                productService.buildProductResponseWithReviewData(slice, userId);

        return new CategoryNewProductResponse(
                middleCategory.name(),
                responseSlice.getContent(),
                PageableResponse.of(responseSlice)
        );
    }

    public CategoryPopularProductResponse searchPopularProductsByCategory(MiddleCategory middleCategory, Long userId,
                                                                          int page,
                                                                          int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Product> slice = productRepository
                .findProductsByPopularityAndRating(middleCategory, pageable);
        Slice<ProductResponse> responseSlice =
                productService.buildProductResponseWithReviewData(slice, userId);

        return new CategoryPopularProductResponse(
                middleCategory.getDisplayName(),
                responseSlice.getContent(),
                PageableResponse.of(responseSlice)
        );
    }

    public ProductDetailResponse getProductDetail(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        List<ProductImage> images = productImageRepository.findByProductIdIn(List.of(productId));
        Map<Long, List<String>> imageUrlsMap = productService.createProductImageUrlsMap(images);
        String joinedUrls = String.join(",", imageUrlsMap.getOrDefault(productId, List.of()));

        List<Object[]> stats = reviewRepository.countAndAvgRatingByProductIds(List.of(productId));
        Map<Long, Long> reviewCountMap = new HashMap<>();
        Map<Long, BigDecimal> weightedSumsMap = new HashMap<>();
        Map<Long, Map<Rating, Long>> countsMap = new HashMap<>();
        productService.aggregateReviewStats(
                stats,
                reviewCountMap,
                weightedSumsMap,
                countsMap
        );

        Map<Long, Double> avgRatingMap = productService.calculateAverageRatings(reviewCountMap, weightedSumsMap);
        Map<Long, ProductSummary> summaryMap = productService.createProductSummaryMap(List.of(product),
                Map.of(productId, joinedUrls),
                reviewCountMap,
                avgRatingMap
        );

        List<ProductResponse> products =
                productService.makeProductResponse(List.of(product), summaryMap, userId);
        List<ProductOption> options = productOptionRepository.findByProduct(product);
        List<ProductOptionResponse> optionResponses = options.stream()
                .map(ProductOptionResponse::from)
                .toList();

        ProductResponse productResponse = products.stream().findFirst()
                .orElseThrow(ProductNotFoundException::new);

        return ProductDetailResponse.from(productResponse, optionResponses, product);
    }

    public ProductDetailYoutubeResponse getProductDetailYoutube(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        String raw = product.getYoutubeUrl();
        if (raw == null || raw.isBlank()) {
            return new ProductDetailYoutubeResponse(null);
        }
        List<String> urls = Arrays.stream(raw.split(","))
                .map(String::trim)
                .toList();

        return new ProductDetailYoutubeResponse(urls);
    }
}
