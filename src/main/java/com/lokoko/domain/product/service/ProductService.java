package com.lokoko.domain.product.service;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.dto.CategoryProductResponse;
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
    private final ProductReadService productReadService
    private final KuromojiService kuromojiService;

    // 카테고리 id 로 제품 리스트 조회
    public CategoryProductResponse searchProductsByCategory(String middleCategoryId, String subCategoryId) {

        MiddleCategory middleCategory = productReadService.getMiddleCategory(middleCategoryId);

        List<Product> products;

        SubCategory subCategory = null;

        // 서브 카테고리 id 가 존재하는 경우
        if (subCategoryId != null && !subCategoryId.isBlank()) {
            subCategory = productReadService.getSubCategory(subCategoryId);
            // middle 카테고리 + sub 카테고리 조합으로 검색
            products = productRepository.findByMiddleCategoryAndSubCategory(middleCategory, subCategory);
        } else { // 서브 카테고리 id 가 존재하지 않는 경우
            // middle 카테고리만으로 검색
            products = productRepository.findByMiddleCategory(middleCategory);
        }

        List<ProductResponse> productResponses = buildProductResponseWithReviewData(products);

        // 최종 DTO 반환
        if (subCategory == null) { // Middle 카테고리만으로 검색 한 경우
            return new CategoryProductResponse(
                    middleCategory.getDisplayName(),
                    middleCategory.getParent().getDisplayName(),
                    middleCategory.getDisplayName(),
                    products.size(),
                    productResponses);

        }

        // Middle, Sub 카테고리 모두 사용하여 검색 한 경우
        return new CategoryProductResponse(
                subCategory.getDisplayName(), //사용자의 검색어 (searchQuery)
                subCategory.getMiddleCategory().getParent().getDisplayName(), // 서브 카테고리 부모 이름
                subCategory.getDisplayName(), //사용자가 검색한 서브 카테고리 이름
                products.size(), // 검색 결과 상품 수
                productResponses); // 검색 결과 상품 list

    }

    public NameBrandProductResponse search(String keyword) {

        List<String> tokens = kuromojiService.tokenize(keyword);
        List<Product> products = productRepository.searchByTokens(tokens);

        List<ProductResponse> productResponses = buildProductResponseWithReviewData(products);

        return new NameBrandProductResponse(
                keyword,
                products.size(),
                productResponses
        );

    }

    private List<ProductResponse> buildProductResponseWithReviewData(List<Product> products) {
        // 제품과 관련있는 이미지들을 한번에 in 쿼리로 받아오기 위해, product 의 Id 만 리스트로 가져온다.
        List<Long> productIds = getProductIds(products);

        // product id 의 리스트를 매개변수로 하여 이와 관련 있는 이미지를 in 쿼리로 한번에 가져오는 메소드
        // N+1 문제 발생하지 않는다.
        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);

        // productIdToImageUrl 에는, 하나의 제품에 대한 대표 이미지가 포함되어있다.
        // 즉, Long 은 제품의 ID 이고, String 은 해당 제품의 대표 이미지의 URL 이다.
        Map<Long, String> productIdToImageUrl = createProductImageMap(images);

        // Review 테이블에서 product id 의 리스트를 in 쿼리에 넣어 관련 있는 리뷰를 조회하고,
        // 조회된 리뷰는 product id 를 기준으로 그룹핑 한다.
        // count 쿼리를 통해서 같은 제품에 대한 리뷰의 개수를 집계할 수 있다.
        // 역시 IN 쿼리를 이용하므로 N+1 문제 발생 X

        List<Object[]> reviewStats = reviewRepository.countAndAvgRatingByProductIds(productIds);
        // reviewStats 안에 있는 Object 배열의 구성요소
        // Object[0] 은 product id , Object[1] 은 리뷰의 평점  , Object[2] 는 리뷰의 개수

        // (제품id : 리뷰수) Map
        Map<Long, Long> productIdToReviewCount = new HashMap<>();

        // (제품id : 평균별점) Map
        Map<Long, BigDecimal> productIdToAvgRating = new HashMap<>();

        Map<Long, Map<Rating, Long>> tempRatingCounts = new HashMap<>();
        Map<Long, BigDecimal> tempWeightedSums = new HashMap<>();

        // 리뷰 통계 집계
        aggregateReviewStats(reviewStats, productIdToReviewCount, tempWeightedSums, tempRatingCounts);

        // 평균 별점 계산
        calculateAverageRatings(productIdToReviewCount, tempWeightedSums, tempRatingCounts, productIdToAvgRating);

        // 리뷰 수를 기준으로 내림차순 정렬(리뷰 수가 동일 할 경우 평균 별점이 높은 순으로 정렬)
        sortProductByReviewCountAndRating(products, productIdToReviewCount, productIdToAvgRating);

        Map<Long, ProductSummary> summaryMap = createProductSummaryMap(products,
                productIdToImageUrl, productIdToReviewCount, productIdToAvgRating);

        // 최종적으로 클라이언트에게 반환될 DTO를 만드는 과정
        List<ProductResponse> productResponses = makeProductResponse(products, summaryMap);

        return productResponses;
    }

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

    private MiddleCategory getMiddleCategory(String middleCategoryId) {
        return Arrays.stream(MiddleCategory.values())
                .filter(mid -> mid.getCtgrNo().equals(middleCategoryId))
                .findFirst()
                .orElseThrow(MiddleCategoryNotFoundException::new);
    }
    // 클라이언트에서 카테고리 number 를 전달하므로, 이 number 에 해당하는 카테고리 이름을 검색해야함.

    private SubCategory getSubCategory(String subCategoryId) {
        return Arrays.stream(SubCategory.values())
                .filter(sub -> sub.getCtgrNo().equals(subCategoryId))
                .findFirst()
                .orElseThrow(SubCategoryNotFoundException::new);
    }
    // 제품을 내림차순(리뷰 수 기준)으로 정렬하는 메소드. 리뷰 수가 같을 경우 평균 별점 내림차순으로 정렬

    private void sortProductByReviewCountAndRating(List<Product> products, Map<Long, Long> reviewCountMap,
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
