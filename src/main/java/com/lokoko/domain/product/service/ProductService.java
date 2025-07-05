package com.lokoko.domain.product.service;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.dto.NameBrandProductResponse;
import com.lokoko.domain.product.dto.ProductResponse;
import com.lokoko.domain.product.dto.ProductSummary;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.exception.MiddleCategoryNotFoundException;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.exception.SubCategoryNotFoundException;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.kuromoji.service.KuromojiService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;
    private final KuromojiService kuromojiService;

    public NameBrandProductResponse search(String keyword) {
        List<String> tokens = kuromojiService.tokenize(keyword);
        List<Product> products = productRepository.searchByTokens(tokens);
        List<ProductResponse> responses = buildProductResponseWithReviewData(products);
        return new NameBrandProductResponse(keyword, products.size(), responses);
    }

    private List<ProductResponse> buildProductResponseWithReviewData(List<Product> products) {
        // 1) ID 리스트
        List<Long> productIds = getProductIds(products);

        // 2) 이미지 조회 → 맵 생성
        Map<Long, String> imageMap = createProductImageMap(
                productImageRepository.findByProductIdIn(productIds)
        );

        // 3) 리뷰 통계 조회
        List<Object[]> stats = reviewRepository.countAndAvgRatingByProductIds(productIds);
        Map<Long, Long> productIdToReviewCount = new HashMap<>();
        Map<Long, BigDecimal> tempWeightedSums = new HashMap<>();
        Map<Long, Map<Rating, Long>> tempRatingCounts = new HashMap<>();
        aggregateReviewStats(stats, productIdToReviewCount, tempWeightedSums, tempRatingCounts);

        // 4) 평균 별점 계산
        Map<Long, BigDecimal> productIdToAvgRating = new HashMap<>();
        calculateAverageRatings(productIdToReviewCount, tempWeightedSums, tempRatingCounts, productIdToAvgRating);

        // 5) 정렬
        sortProductByReviewCountAndRating(products, productIdToReviewCount, productIdToAvgRating);

        // 6) summary 맵 생성
        Map<Long, ProductSummary> summaryMap = createProductSummaryMap(
                products, imageMap, productIdToReviewCount, productIdToAvgRating
        );

        // 7) 최종 DTO 변환
        return makeProductResponse(products, summaryMap);
    }

    public void aggregateReviewStats(List<Object[]> reviewStats,
                                     Map<Long, Long> productIdToReviewCount,
                                     Map<Long, BigDecimal> tempWeightedSums,
                                     Map<Long, Map<Rating, Long>> tempRatingCounts) {
        for (Object[] row : reviewStats) {
            Long productId = (Long) row[0];
            Rating rating = (Rating) row[1];
            Long count = (Long) row[2];

            productIdToReviewCount.merge(productId, count, Long::sum);

            BigDecimal weightedValue = valueOf(Integer.parseInt(rating.getValue()))
                    .multiply(valueOf(count));
            tempWeightedSums.merge(productId, weightedValue, BigDecimal::add);

            tempRatingCounts.computeIfAbsent(productId, k -> new EnumMap<>(Rating.class))
                    .put(rating, count);
        }
    }

    public void calculateAverageRatings(Map<Long, Long> productIdToReviewCount,
                                        Map<Long, BigDecimal> tempWeightedSums,
                                        Map<Long, Map<Rating, Long>> tempRatingCounts,
                                        Map<Long, BigDecimal> productIdToAvgRating) {
        for (Map.Entry<Long, Long> entry : productIdToReviewCount.entrySet()) {
            Long productId = entry.getKey();
            Long totalReviews = entry.getValue();
            BigDecimal weightedSum = tempWeightedSums.get(productId);

            BigDecimal avg = totalReviews > 0
                    ? weightedSum.divide(valueOf(totalReviews), 1, RoundingMode.HALF_UP)
                    : ZERO;
            productIdToAvgRating.put(productId, avg);

            Map<Rating, Long> ratingCounts = tempRatingCounts.get(productId);
            if (ratingCounts != null) {
                calculateRatingRatioForProduct(ratingCounts, totalReviews, productId);
            }
        }
    }

    public Map<Long, ProductSummary> createProductSummaryMap(List<Product> products,
                                                             Map<Long, String> productIdToImageUrl,
                                                             Map<Long, Long> productIdToReviewCount,
                                                             Map<Long, BigDecimal> productIdToAvgRating) {
        Map<Long, ProductSummary> summaryMap = new HashMap<>();
        for (Product product : products) {
            Long productId = product.getId();
            String imageUrl = productIdToImageUrl.getOrDefault(productId, null);
            Long reviewCount = productIdToReviewCount.getOrDefault(productId, 0L);
            BigDecimal avg = productIdToAvgRating.getOrDefault(productId, ZERO);

            summaryMap.put(productId, new ProductSummary(imageUrl, reviewCount, avg));
        }
        return summaryMap;
    }

    public List<ProductResponse> makeProductResponse(List<Product> products,
                                                     Map<Long, ProductSummary> summaryMap) {
        return products.stream()
                .map(product -> {
                    ProductSummary s = summaryMap.getOrDefault(
                            product.getId(),
                            new ProductSummary(null, 0L, ZERO)
                    );
                    return new ProductResponse(
                            product.getId(),
                            s.imageUrl(),
                            product.getProductName(),
                            s.reviewCount(),
                            s.avgRating()
                    );
                })
                .toList();
    }

    public Map<Long, String> createProductImageMap(List<ProductImage> images) {
        return images.stream()
                .collect(groupingBy(
                        img -> img.getProduct().getId(),
                        collectingAndThen(toList(), list ->
                                list.stream()
                                        .filter(ProductImage::isMain)
                                        .findFirst()
                                        .orElse(list.get(0))
                                        .getUrl()
                        )
                ));
    }

    public void sortProductByReviewCountAndRating(List<Product> products,
                                                  Map<Long, Long> reviewCountMap,
                                                  Map<Long, BigDecimal> ratingMap) {
        products.sort((p1, p2) -> {
            Long c1 = reviewCountMap.getOrDefault(p1.getId(), 0L);
            Long c2 = reviewCountMap.getOrDefault(p2.getId(), 0L);
            int cmp = c2.compareTo(c1);
            if (cmp == 0) {
                BigDecimal r1 = ratingMap.getOrDefault(p1.getId(), ZERO);
                BigDecimal r2 = ratingMap.getOrDefault(p2.getId(), ZERO);
                return r2.compareTo(r1);
            }
            return cmp;
        });
    }

    public List<Long> getProductIds(List<Product> products) {
        return products.stream()
                .map(p -> {
                    if (p == null || p.getId() == null) {
                        throw new ProductNotFoundException();
                    }
                    return p.getId();
                })
                .toList();
    }

    private MiddleCategory getMiddleCategory(String middleCategoryId) {
        return Arrays.stream(MiddleCategory.values())
                .filter(mid -> mid.getCtgrNo().equals(middleCategoryId))
                .findFirst()
                .orElseThrow(MiddleCategoryNotFoundException::new);
    }

    private SubCategory getSubCategory(String subCategoryId) {
        return Arrays.stream(SubCategory.values())
                .filter(sub -> sub.getCtgrNo().equals(subCategoryId))
                .findFirst()
                .orElseThrow(SubCategoryNotFoundException::new);
    }

    public void calculateRatingRatioForProduct(Map<Rating, Long> ratingCounts, long totalReviews, Long productId) {
        Map<Long, Map<Rating, BigDecimal>> productIdToRatios = new HashMap<>();
        Map<Rating, BigDecimal> ratios = new EnumMap<>(Rating.class);

        for (Map.Entry<Rating, Long> e : ratingCounts.entrySet()) {
            BigDecimal ratio = valueOf(e.getValue())
                    .multiply(valueOf(100))
                    .divide(valueOf(totalReviews), 1, RoundingMode.HALF_UP);
            ratios.put(e.getKey(), ratio);
        }
        productIdToRatios.put(productId, ratios);
    }
}