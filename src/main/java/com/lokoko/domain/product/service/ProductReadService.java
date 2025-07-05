package com.lokoko.domain.product.service;


import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.dto.CategoryNewProductResponse;
import com.lokoko.domain.product.dto.CategoryProductResponse;
import com.lokoko.domain.product.dto.ProductResponse;
import com.lokoko.domain.product.dto.ProductSummary;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import com.lokoko.domain.product.exception.MiddleCategoryNotFoundException;
import com.lokoko.domain.product.exception.SubCategoryNotFoundException;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.repository.ReviewRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReadService {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;

    // 카테고리 id 로 제품 리스트 조회
    public CategoryProductResponse searchProductsByCategory(String middleCategoryId, String subCategoryId) {
        MiddleCategory middleCategory = getMiddleCategory(middleCategoryId);
        List<Product> products;
        SubCategory subCategory = null;

        if (subCategoryId != null && !subCategoryId.isBlank()) {
            subCategory = getSubCategory(subCategoryId);
            // middle 카테고리 + sub 카테고리 조합으로 검색
            products = productRepository.findByMiddleCategoryAndSubCategory(middleCategory, subCategory);
        } else { // 서브 카테고리 id 가 존재하지 않는 경우
            // middle 카테고리만으로 검색
            products = productRepository.findByMiddleCategory(middleCategory);
        }
        List<Long> productIds = productService.getProductIds(products);
        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);
        Map<Long, String> productIdToImageUrl = productService.createProductImageMap(images);

        List<Object[]> reviewStats = reviewRepository.countAndAvgRatingByProductIds(productIds);

        Map<Long, Long> productIdToReviewCount = new HashMap<>();
        Map<Long, BigDecimal> productIdToAvgRating = new HashMap<>();
        Map<Long, Map<Rating, Long>> tempRatingCounts = new HashMap<>();
        Map<Long, BigDecimal> tempWeightedSums = new HashMap<>();
        productService.aggregateReviewStats(reviewStats, productIdToReviewCount, tempWeightedSums, tempRatingCounts);

        productService.calculateAverageRatings(productIdToReviewCount, tempWeightedSums, tempRatingCounts,
                productIdToAvgRating);
        productService.sortProductByReviewCountAndRating(products, productIdToReviewCount, productIdToAvgRating);

        Map<Long, ProductSummary> summaryMap = productService.createProductSummaryMap(products,
                productIdToImageUrl, productIdToReviewCount, productIdToAvgRating);

        List<ProductResponse> productResponses = productService.makeProductResponse(products, summaryMap);

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

    public CategoryNewProductResponse searchNewProductsByCategory(String middleCategoryId) {
        MiddleCategory middleCategory = getMiddleCategory(middleCategoryId);
        List<Product> products = productRepository.findByMiddleCategoryAndTag(middleCategory, Tag.NEW);

        List<Long> productIds = productService.getProductIds(products);
        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);
        Map<Long, String> productIdToImageUrl = productService.createProductImageMap(images);

        List<Object[]> reviewStats = reviewRepository.countAndAvgRatingByProductIds(productIds);

        Map<Long, Long> productIdToReviewCount = new HashMap<>();
        Map<Long, BigDecimal> productIdToAvgRating = new HashMap<>();
        Map<Long, Map<Rating, Long>> tempRatingCounts = new HashMap<>();
        Map<Long, BigDecimal> tempWeightedSums = new HashMap<>();

        productService.aggregateReviewStats(reviewStats, productIdToReviewCount, tempWeightedSums, tempRatingCounts);
        productService.calculateAverageRatings(productIdToReviewCount, tempWeightedSums, tempRatingCounts,
                productIdToAvgRating);
        productService.sortProductByReviewCountAndRating(products, productIdToReviewCount, productIdToAvgRating);
        Map<Long, ProductSummary> summaryMap = productService.createProductSummaryMap(products, productIdToImageUrl,
                productIdToReviewCount, productIdToAvgRating);
        List<ProductResponse> productResponses = productService.makeProductResponse(products, summaryMap);

        return new CategoryNewProductResponse(
                middleCategory.name(),
                productResponses
        );
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
}
