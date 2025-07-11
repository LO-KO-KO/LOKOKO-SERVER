package com.lokoko.domain.product.repository;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    long countByMainCategoryAndMiddleCategoryAndSubCategory(MainCategory mainCategory, MiddleCategory middleCategory,
                                                            SubCategory subCategory);

    List<Product> findBySubCategory(SubCategory subCategory);

    @Query("SELECT p FROM Product p WHERE p.oliveYoungUrl IS NOT NULL")
    List<Product> findAllByOliveYoungUrlNotNull();

    Slice<Product> findByMiddleCategoryAndSubCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                      Pageable pageable);

    Slice<Product> findByMiddleCategory(MiddleCategory middleCategory, Pageable pageable);

    Slice<Product> findByMiddleCategoryAndTag(MiddleCategory middleCategory, Tag tag,
                                              Pageable pageable);
}
