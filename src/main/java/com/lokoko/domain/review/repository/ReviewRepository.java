package com.lokoko.domain.review.repository;

import com.lokoko.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r.product.id, COUNT(r) FROM Review r WHERE r.product.id IN :procutIds GROUP BY r.product.id")
    List<Object[]> countReviewsByProductIds(@Param("productIds") List<Long> productIds);

}
