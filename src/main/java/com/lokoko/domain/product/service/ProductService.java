package com.lokoko.domain.product.service;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.product.dto.ProductResponse;
import com.lokoko.domain.product.dto.ProductSummary;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.review.entity.enums.Rating;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public void aggregateReviewStats(List<Object[]> reviewStats,
                                     Map<Long, Long> productIdToReviewCount,
                                     Map<Long, BigDecimal> tempWeightedSums,
                                     Map<Long, Map<Rating, Long>> tempRatingCounts) {

        for (Object[] row : reviewStats) {
            Long productId = (Long) row[0];
            Rating rating = (Rating) row[1];
            Long count = (Long) row[2];

            // 리뷰 수 계산
            productIdToReviewCount.merge(productId, count, Long::sum);

            // 평점의 합 계산
            BigDecimal weightedValue = valueOf(Integer.parseInt(rating.getValue()))
                    .multiply(valueOf(count));
            tempWeightedSums.merge(productId, weightedValue, BigDecimal::add);

            // 별점별 개수 저장
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
            BigDecimal avgRating = productIdToAvgRating.getOrDefault(productId, ZERO);

            summaryMap.put(productId, new ProductSummary(imageUrl, reviewCount, avgRating));

        }
        return summaryMap;
    }

    // 현재는, 사용되지 않으나 상품 상세조회에서 사용됨
    public void calculateRatingRatioForProduct(Map<Rating, Long> ratingCounts, long totalReviews,
                                               Long productId) {
        // 상품의 별점별 비율 계산 하기
        // ex) (상품1 : (1점 : 0.1), (2점 : 0.3) ......)
        Map<Long, Map<Rating, BigDecimal>> productIdToRatingRatios = new HashMap<>();
        // 별점별 비율 저장하기 위한 map
        // ex) ( (1점 : 0.1), (2점 : 0.2 ) ,,,,, )
        Map<Rating, BigDecimal> ratios = new EnumMap<>(Rating.class);

        for (Map.Entry<Rating, Long> ratingEntry : ratingCounts.entrySet()) {
            Rating rating = ratingEntry.getKey(); // 별점 값
            Long count = ratingEntry.getValue(); // 그 별점의 개수 

            BigDecimal ratio = valueOf(count)
                    .multiply(valueOf(100))
                    .divide(valueOf(totalReviews), 1, RoundingMode.HALF_UP);

            ratios.put(rating, ratio);
        }
        productIdToRatingRatios.put(productId, ratios);
    }

    public List<ProductResponse> makeProductResponse(List<Product> products,
                                                     Map<Long, ProductSummary> summaryMap) {
        return products.stream()
                .map(product -> {

                    ProductSummary summary = summaryMap.getOrDefault(product.getId(),
                            new ProductSummary(null, 0L, ZERO));

                    return new ProductResponse(
                            product.getId(),
                            summary.imageUrl(),
                            product.getProductName(),
                            summary.reviewCount(),
                            summary.avgRating()
                    );
                })
                .toList();
    }

    public List<Long> getProductIds(List<Product> products) {
        return products.stream()
                .map(product -> {
                    if (product == null || product.getId() == null) {
                        throw new ProductNotFoundException();
                    }
                    return product.getId();
                })
                .toList();
    }

    public Map<Long, String> createProductImageMap(List<ProductImage> images) {
        return images.stream()
                .collect(groupingBy(
                        img -> img.getProduct().getId(),
                        collectingAndThen(
                                toList(), // ProductImage 객체들을 리스트로 수집
                                imageList -> {
                                    return imageList.stream()
                                            .filter(ProductImage::isMain) // 제품의 대표이미지를 먼저 가져옴
                                            .findFirst()
                                            .orElse(imageList.get(0))// 대표이미지 설정 안되어 있다면 리스트의 첫 번째 이미지
                                            .getUrl();
                                }
                        )
                ));
    }

    // 제품을 내림차순(리뷰 수 기준)으로 정렬하는 메소드. 리뷰 수가 같을 경우 평균 별점 내림차순으로 정렬
    public void sortProductByReviewCountAndRating(List<Product> products, Map<Long, Long> reviewCountMap,
                                                  Map<Long, BigDecimal> ratingMap) {
        products.sort((p1, p2) -> {

            Long count1 = reviewCountMap.getOrDefault(p1.getId(), 0L);
            Long count2 = reviewCountMap.getOrDefault(p2.getId(), 0L);

            int tmp = count2.compareTo(count1); // 리뷰 수를 내림 차순정렬

            if (tmp == 0) { // 만약, 리뷰 수가 같은 경우
                BigDecimal rating1 = ratingMap.getOrDefault(p1.getId(), ZERO);
                BigDecimal rating2 = ratingMap.getOrDefault(p2.getId(), ZERO);
                return rating2.compareTo(rating1);
            }

            return tmp;
        });

    }
}
