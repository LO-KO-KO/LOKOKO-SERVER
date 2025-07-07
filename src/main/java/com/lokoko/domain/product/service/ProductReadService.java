package com.lokoko.domain.product.service;


import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.dto.CategoryNewProductResponse;
import com.lokoko.domain.product.dto.CategoryProductResponse;
import com.lokoko.domain.product.dto.ProductDetailResponse;
import com.lokoko.domain.product.dto.ProductDetailYoutubeResponse;
import com.lokoko.domain.product.dto.ProductResponse;
import com.lokoko.domain.product.dto.ProductSummary;
import com.lokoko.domain.product.entity.Product;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReadService {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ReviewRepository reviewRepository;

    // 카테고리 id 로 제품 리스트 조회
    public CategoryProductResponse searchProductsByCategory(String middleCategoryId, String subCategoryId,
                                                            Integer page, Integer size) {
        MiddleCategory middleCategory = getMiddleCategory(middleCategoryId);
        Slice<Product> productSlice;
        SubCategory subCategory = null;

        Pageable pageable = PageRequest.of(page, size);

        if (subCategoryId != null && !subCategoryId.isBlank()) {
            subCategory = getSubCategory(subCategoryId);
            // middle 카테고리 + sub 카테고리 조합으로 검색
            productSlice = productRepository.findByMiddleCategoryAndSubCategory(middleCategory, subCategory, pageable);
        } else { // 서브 카테고리 id 가 존재하지 않는 경우
            // middle 카테고리만으로 검색
            productSlice = productRepository.findByMiddleCategory(middleCategory, pageable);
        }

        List<Product> products = productSlice.getContent();

        List<Long> productIds = getProductIds(products);
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
            return CategoryProductResponse.of(
                    middleCategory.getDisplayName(),
                    middleCategory.getParent().getDisplayName(),
                    middleCategory.getDisplayName(),
                    productSlice,
                    productResponses
            );

        }
        // Middle, Sub 카테고리 모두 사용하여 검색 한 경우
        return CategoryProductResponse.of(
                subCategory.getDisplayName(), //사용자의 검색어 (searchQuery)
                subCategory.getMiddleCategory().getParent().getDisplayName(), // 서브 카테고리 부모 이름
                subCategory.getDisplayName(),
                productSlice,
                productResponses
        ); // 검색 결과 상품 list
    }

    public CategoryNewProductResponse searchNewProductsByCategory(String middleCategoryId) {
        MiddleCategory middleCategory = getMiddleCategory(middleCategoryId);
        List<Product> products = productRepository.findByMiddleCategoryAndTag(middleCategory, Tag.NEW);

        List<Long> productIds = getProductIds(products);
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

    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        List<Long> productIds = List.of(productId);
        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);
        Map<Long, List<String>> imageUrlsMap = productService.createProductImageUrlsMap(images);
        List<String> imageUrls = imageUrlsMap.get(productId);

        List<Object[]> stats = reviewRepository.countAndAvgRatingByProductIds(List.of(productId));
        Map<Long, Long> reviewCountMap = new HashMap<>();
        Map<Long, BigDecimal> weightedSums = new HashMap<>();
        Map<Long, Map<Rating, Long>> counts = new HashMap<>();
        productService.aggregateReviewStats(stats, reviewCountMap, weightedSums, counts);

        Map<Long, BigDecimal> avgRatingMap = new HashMap<>();
        productService.calculateAverageRatings(reviewCountMap, weightedSums, counts, avgRatingMap);

        Map<Long, String> imageUrlsStringMap = new HashMap<>();
        imageUrlsStringMap.put(productId, String.join(",", imageUrls));

        List<ProductResponse> products = productService.makeProductResponse(
                List.of(product),
                productService.createProductSummaryMap(
                        List.of(product),
                        imageUrlsStringMap,
                        reviewCountMap,
                        avgRatingMap
                )
        );

        List<String> optionNames = getProductOptionNames(product);

        return new ProductDetailResponse(
                products,
                optionNames,
                imageUrls,
                product.getBrandName(),
                product.getNormalPrice(),
                product.getProductDetail(),
                product.getUnit(),
                product.getIngredients(),
                product.getShippingInfo(),
                product.getOliveYoungUrl(),
                product.getQoo10Url(),
                product.getMiddleCategory(),
                product.getSubCategory()
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

    public List<String> getProductOptionNames(Product product) {
        List<String> names = productOptionRepository.findOptionNamesByProduct(product);
        return names.isEmpty() ? null : names;
    }

    public List<Long> getProductIds(List<Product> products) {
        return products.stream()
                .map(Product::getId)
                .toList();
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
