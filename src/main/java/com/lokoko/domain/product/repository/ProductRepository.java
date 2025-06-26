package com.lokoko.domain.product.repository;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByMainCategoryAndMiddleCategoryAndSubCategory(MainCategory mainCategory, MiddleCategory middleCategory,
                                                            SubCategory subCategory);
}
