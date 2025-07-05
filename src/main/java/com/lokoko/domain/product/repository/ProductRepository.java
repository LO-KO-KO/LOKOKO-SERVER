package com.lokoko.domain.product.repository;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByMainCategoryAndMiddleCategoryAndSubCategory(MainCategory mainCategory, MiddleCategory middleCategory,
                                                            SubCategory subCategory);

    List<Product> findBySubCategory(SubCategory subCategory);

    @Query("SELECT p FROM Product p WHERE p.oliveYoungUrl IS NOT NULL")
    List<Product> findAllByOliveYoungUrlNotNull();

    List<Product> findByMiddleCategoryAndSubCategory(MiddleCategory middleCategory, SubCategory subCategory);

    List<Product> findByMiddleCategory(MiddleCategory middleCategory);

    List<Product> findByMiddleCategoryAndTag(MiddleCategory middleCategory, Tag tag);
}
