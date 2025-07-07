package com.lokoko.domain.product.repository;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositoryCustom {
    Slice<Product> searchByTokens(List<String> tokens, Pageable pageable);

    Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, Pageable pageable);

    Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, SubCategory subCategory,
                                                     Pageable pageable);
}
