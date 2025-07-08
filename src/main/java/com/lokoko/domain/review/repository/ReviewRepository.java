package com.lokoko.domain.review.repository;

import com.lokoko.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("SELECT r.product.id, r.rating , COUNT(r) "
            + "FROM Review r "
            + "WHERE r.product.id IN :productIds "
            + "GROUP BY r.product.id, r.rating")
    List<Object[]> countAndAvgRatingByProductIds(@Param("productIds") List<Long> productIds);
}
