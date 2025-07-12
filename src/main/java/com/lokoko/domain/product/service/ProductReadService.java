package com.lokoko.domain.product.service;


import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.like.service.ProductLikeService;
import com.lokoko.domain.product.dto.response.CategoryNewProductResponse;
import com.lokoko.domain.product.dto.response.CategoryPopularProductResponse;
import com.lokoko.domain.product.dto.response.CategoryProductPageResponse;
import com.lokoko.domain.product.dto.response.ProductDetailResponse;
import com.lokoko.domain.product.dto.response.ProductDetailYoutubeResponse;
import com.lokoko.domain.product.dto.response.ProductOptionResponse;
import com.lokoko.domain.product.dto.response.ProductResponse;
import com.lokoko.domain.product.dto.response.ProductSummary;
import com.lokoko.domain.product.dto.response.ScorePercent;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.repository.ProductOptionRepository;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
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
    private final ProductLikeService productLikeService;
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
        List<RatingCount> stats = reviewRepository.countByProductIdsAndRating(List.of(productId));
        long totalCount = 0L;
        Map<Rating, Long> countMap = new HashMap<>();
        long weightedSum = 0L;

        for (RatingCount rc : stats) {
            Rating rating = rc.rating();
            Long cnt = rc.count();

            countMap.put(rating, cnt);
            totalCount += cnt;
            weightedSum += rating.getValue() * cnt;
        }
        double rawAvg = totalCount == 0
                ? 0.0
                : (double) weightedSum / totalCount;
        double avgRating = Math.round(rawAvg * 10) / 10.0;
        Map<Long, Long> reviewCountMap = Map.of(productId, totalCount);
        Map<Long, Double> avgRatingMap = Map.of(productId, avgRating);
        Map<Long, ProductSummary> summaryMap = productService.createProductSummaryMap(
                List.of(product),
                Map.of(productId, joinedUrls),
                reviewCountMap,
                avgRatingMap
        );
        ProductResponse productResponse = productService.makeProductResponse(
                List.of(product), summaryMap, userId
        ).stream().findFirst().orElseThrow(ProductNotFoundException::new);
        List<ProductOptionResponse> optionResponses = productOptionRepository.findByProduct(product)
                .stream().map(ProductOptionResponse::from).toList();

        long cnt5 = countMap.getOrDefault(Rating.FIVE, 0L);
        long cnt4 = countMap.getOrDefault(Rating.FOUR, 0L);
        long cnt3 = countMap.getOrDefault(Rating.THREE, 0L);
        long cnt2 = countMap.getOrDefault(Rating.TWO, 0L);
        long cnt1 = countMap.getOrDefault(Rating.ONE, 0L);
        long pct5 = totalCount == 0 ? 0L : (cnt5 * 100) / totalCount;
        long pct4 = totalCount == 0 ? 0L : (cnt4 * 100) / totalCount;
        long pct3 = totalCount == 0 ? 0L : (cnt3 * 100) / totalCount;
        long pct2 = totalCount == 0 ? 0L : (cnt2 * 100) / totalCount;
        long pct1 = totalCount == 0 ? 0L : (cnt1 * 100) / totalCount;
        List<ScorePercent> starPercent = List.of(
                new ScorePercent(5, pct5),
                new ScorePercent(4, pct4),
                new ScorePercent(3, pct3),
                new ScorePercent(2, pct2),
                new ScorePercent(1, pct1)
        );
        boolean isLiked = productLikeService.isLiked(productId, userId);

        return ProductDetailResponse.from(
                productResponse,
                optionResponses,
                product,
                starPercent,
                isLiked
        );
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
