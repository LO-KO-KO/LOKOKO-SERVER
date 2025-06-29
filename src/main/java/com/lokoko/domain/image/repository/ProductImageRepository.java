package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdIn(List<Long> productIds);
}
