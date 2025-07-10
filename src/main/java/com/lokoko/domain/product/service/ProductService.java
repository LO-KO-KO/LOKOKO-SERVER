package com.lokoko.domain.product.service;

import static java.math.BigDecimal.valueOf;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.dto.response.NameBrandProductResponse;
import com.lokoko.domain.product.dto.response.ProductResponse;
import com.lokoko.domain.product.dto.response.ProductSummary;
import com.lokoko.domain.like.repository.ProductLikeRepository;
import com.lokoko.domain.product.dto.NameBrandProductResponse;
import com.lokoko.domain.product.dto.ProductResponse;
import com.lokoko.domain.product.dto.ProductSummary;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.kuromoji.service.KuromojiService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ReviewRepository reviewRepository;
    private final KuromojiService kuromojiService;

    public NameBrandProductResponse search(String keyword, int page, int size, Long userId) {
        List<String> tokens = kuromojiService.tokenize(keyword);
        Pageable pageable = PageRequest.of(page, size);
        Slice<Product> slice = productRepository.searchByTokens(tokens, pageable);
        Slice<ProductResponse> responseSlice = buildProductResponseWithReviewData(slice, userId);

        return new NameBrandProductResponse(
                keyword,
                responseSlice.getContent(),
                PageableResponse.of(responseSlice)
        );
    }

    public List<ProductResponse> buildProductResponseWithReviewData(List<Product> products, Long userId) {
        // 1) ID 추출
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        // 2) 이미지 조회 → 맵 생성
        Map<Long, String> imageMap = createProductImageMap(
                productImageRepository.findByProductIdIn(productIds)
        );

        // 3) 리뷰 통계 조회
        List<Object[]> stats = reviewRepository.countAndAvgRatingByProductIds(productIds);
        Map<Long, Long> reviewCountMap = new HashMap<>();
        Map<Long, BigDecimal> weightedSumsMap = new HashMap<>();
        Map<Long, Map<Rating, Long>> ratingCountsMap = new HashMap<>();
        aggregateReviewStats(stats,
                reviewCountMap,
                weightedSumsMap,
                ratingCountsMap);

        // 4) 평균 별점 계산
        Map<Long, Double> avgMap =
                calculateAverageRatings(reviewCountMap, weightedSumsMap);

        // 5) 정렬
        // 4) 이제 ratingCountsMap 과 avgMap 을 각각 사용 가능
        Map<Long, ProductSummary> summaryMap =
                createProductSummaryMap(
                        products,
                        imageMap,
                        reviewCountMap,
                        avgMap
                );

        // 6) summary 맵 생성
        return makeProductResponse(products, summaryMap, userId);
    }

    public Slice<ProductResponse> buildProductResponseWithReviewData(Slice<Product> slice, Long userId) {
        List<ProductResponse> content = buildProductResponseWithReviewData(slice.getContent(), userId);

        return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
    }

    public void aggregateReviewStats(List<Object[]> reviewStats,
                                     Map<Long, Long> productIdToReviewCount,
                                     Map<Long, BigDecimal> tempWeightedSums,
                                     Map<Long, Map<Rating, Long>> tempRatingCounts) {
        log.debug("→ aggregateReviewStats 시작: reviewStatsSize={}", reviewStats.size());
        for (Object[] row : reviewStats) {
            Long productId = (Long) row[0];
            Rating rating = (Rating) row[1];
            Long count = (Long) row[2];

            productIdToReviewCount.merge(productId, count, Long::sum);

            BigDecimal weightedValue = BigDecimal
                    .valueOf(rating.getValue())
                    .multiply(BigDecimal.valueOf(count));
            tempWeightedSums.merge(productId, weightedValue, BigDecimal::add);

            tempRatingCounts
                    .computeIfAbsent(productId, k -> new EnumMap<>(Rating.class))
                    .merge(rating, count, Long::sum);
        }
        log.info("→ aggregateReviewStats 완료: productIdToReviewCount={}, tempWeightedSums={}, tempRatingCounts={}",
                productIdToReviewCount, tempWeightedSums, tempRatingCounts);
    }

    public Map<Long, Double> calculateAverageRatings(
            Map<Long, Long> reviewCount,
            Map<Long, BigDecimal> weightedSums
    ) {
        Map<Long, Double> avgMap = new HashMap<>();
        for (var e : reviewCount.entrySet()) {
            long cnt = e.getValue();
            double avg = cnt == 0
                    ? 0.0
                    : weightedSums.get(e.getKey())
                            .divide(BigDecimal.valueOf(cnt), 1, RoundingMode.HALF_UP)
                            .doubleValue();
            avgMap.put(e.getKey(), avg);
        }
        return avgMap;
    }

    public Map<Long, ProductSummary> createProductSummaryMap(List<Product> products,
                                                             Map<Long, String> productIdToImageUrl,
                                                             Map<Long, Long> productIdToReviewCount,
                                                             Map<Long, Double> productIdToAvgRating) {
        Map<Long, ProductSummary> summaryMap = new HashMap<>();
        for (Product product : products) {
            Long productId = product.getId();
            String imageUrl = productIdToImageUrl.getOrDefault(productId, "");        // null 대신 빈 문자열
            Long reviewCnt = productIdToReviewCount.getOrDefault(productId, 0L);
            Double avg = productIdToAvgRating.getOrDefault(productId, 0.0);

            summaryMap.put(productId, new ProductSummary(imageUrl, reviewCnt, avg));
        }
        return summaryMap;
    }

    public List<ProductResponse> makeProductResponse(List<Product> products,
                                                     Map<Long, ProductSummary> summaryMap, Long userId) {
        Set<Long> likedIds = productLikeRepository.findAllByUserId(userId).stream()
                .map(pl -> pl.getProduct().getId())
                .collect(Collectors.toSet());

        // 2) ProductResponse.of(...) 으로 매핑
        return products.stream()
                .map(product -> {
                    ProductSummary summary = summaryMap.getOrDefault(
                            product.getId(),
                            new ProductSummary("", 0L, 0.0)
                    );
                    boolean isLiked = likedIds.contains(product.getId());
                    return ProductResponse.of(product, summary, isLiked);
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

    public Map<Long, List<String>> createProductImageUrlsMap(List<ProductImage> images) {
        return images.stream()
                .collect(groupingBy(
                        img -> img.getProduct().getId(),
                        mapping(ProductImage::getUrl, toList())
                ));
    }

    public void sortProductByReviewCountAndRating(List<Product> products,
                                                  Map<Long, Long> reviewCountMap,
                                                  Map<Long, Double> ratingMap) {
        products.sort((p1, p2) -> {
            int cmp = reviewCountMap.getOrDefault(p2.getId(), 0L)
                    .compareTo(reviewCountMap.getOrDefault(p1.getId(), 0L));
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(
                    ratingMap.getOrDefault(p2.getId(), 0.0),
                    ratingMap.getOrDefault(p1.getId(), 0.0)
            );
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