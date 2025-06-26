package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
