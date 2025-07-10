package com.lokoko.domain.like.repository;

import com.lokoko.domain.like.entity.ProductLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByProductIdAndUserId(Long productId, Long userId);

    long countByProductId(Long postId);
}
