package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long>, ReviewImageRepositoryCustom {
    void deleteAllByReview(Review review);
}
