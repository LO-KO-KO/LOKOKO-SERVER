package com.lokoko.domain.product.service;

import static com.lokoko.global.common.exception.ErrorCode.PRODUCT_NOT_FOUND;
import static com.lokoko.global.common.exception.ErrorCode.SUBCATEGORY_NOT_FOUND;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.dto.CategoryProductResponse;
import com.lokoko.domain.product.dto.ProductResponse;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.exception.SubCategoryNotFoundException;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.review.repository.ReviewRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    // 카테고리 id 로 제품 리스트 조회
    public CategoryProductResponse searchProductsByCategory(String subCategoryId) {

        // 카테고리 id 를 통해 SubCategory 검색
        SubCategory subCategory = getSubCategory(subCategoryId);

        // SubCategory 를 통해 productRepository 에서 product 리스트 검색
        List<Product> products = productRepository.findBySubCategory(subCategory);

        // 카테고리 검색을 한 결과, 일치하는 product 가 없을 경우 예외 던지기
        validateProductExistence(products.size());

        // 제품과 관련있는 이미지들을 한번에 in 쿼리로 받아오기 위해, product 의 Id 만 리스트로 가져온다.
        List<Long> productIds = products.stream()
                .map(product -> product.getId())
                .toList();

        // product id 의 리스트를 매개변수로 하여 이와 관련 있는 이미지를 in 쿼리로 한번에 가져오는 메소드
        // N+1 문제 발생하지 않는다.
        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);

        // productIdToImageUrl 에는, 하나의 제품에 대한 대표 이미지가 포함되어있다.
        // 즉, Long 은 제품의 ID 이고, String 은 해당 제품의 대표 이미지의 URL 이다.
        Map<Long, String> productIdToImageUrl = images.stream()
                .collect(groupingBy(
                        img -> img.getProduct().getId(),
                        // groupingBy 에서는 같은 product id 에 대해 이미지를 그룹핑한다.
                        mapping(ProductImage::getUrl, collectingAndThen(toList(),
                                list -> list.get(0)))
                        //mapping 에서는, 위에서 그룹핑 된 (product id, image) 쌍에서
                        // 첫번째 이미지 URL 을 가져온다 (즉 대표 이미지를 가져오는 것).
                ));

        // Review 테이블에서 product id 의 리스트를 in 쿼리에 넣어 관련 있는 리뷰를 조회하고,
        // 조회된 리뷰는 product id 를 기준으로 그룹핑 한다.
        // count 쿼리를 통해서 같은 제품에 대한 리뷰의 개수를 집계할 수 있다.
        // 역시 IN 쿼리를 이용하므로 N+1 문제 발생 X
        List<Object[]> reviewCounts = reviewRepository.countReviewsByProductIds(productIds);

        // productId, reviewCount 쌍으로 구성된 Map 만들기
        Map<Long, Long> productIdToReviewCount = reviewCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // 리뷰 수를 기준으로 내림차순 정렬
        sortProductByReviewCount(products, productIdToReviewCount);

        // 최종적으로 클라이언트에게 반환될 DTO를 만드는 과정
        List<ProductResponse> productResponses = products.stream()
                .map(product -> new ProductResponse(
                                product.getId(),
                                productIdToImageUrl.get(product.getId()),
                                product.getProductName(),
                                productIdToReviewCount.getOrDefault(product.getId(), 0L))
                        // product 의 id 를 key 로 Map 의 value 를 검색할 때,
                        // 해당 key 에 대응하는 value 가 없으면 0을 반환
                        // 해당 key 에 대응하는 value 가 존재한다면 그 값을 반환한다.
                )
                .toList();

        // 최종 DTO 반환
        return new CategoryProductResponse(
                subCategory.getDisplayName(), //사용자가 검색한 서브 카테고리 이름
                subCategory.getMiddleCategory().getParent().getDisplayName(), // 서브 카테고리 부모 이름
                subCategory.getDisplayName(), //사용자가 검색한 서브 카테고리 이름
                products.size(), // 검색 결과 상품 수
                productResponses); // 검색 결과 상품 list

    }

    private static void validateProductExistence(int size) {
        if (size == 0) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.getMessage());
        }
    }


    // 클라이언트에서 카테고리 number 를 전달하므로, 이 number 에 해당하는 카테고리 이름을 검색해야함.
    private SubCategory getSubCategory(String subCategoryId) {
        return Arrays.stream(SubCategory.values())
                .filter(sub -> sub.getCtgrNo().equals(subCategoryId))
                .findFirst()
                .orElseThrow(() -> new SubCategoryNotFoundException(SUBCATEGORY_NOT_FOUND.getMessage()));
    }


    // 제품을 내림차순(리뷰 수 기준)으로 정렬하는 메소드
    private void sortProductByReviewCount(List<Product> products, Map<Long, Long> reviewCountMap) {
        products.sort((p1, p2) -> Long.compare(
                // 리뷰 많은 순으로 내림차순 정렬해야하므로, p2 이 먼저, p1 이 이후에.
                reviewCountMap.getOrDefault(p2.getId(), 0L),
                reviewCountMap.getOrDefault(p1.getId(), 0L)
        ));

    }


}
