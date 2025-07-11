package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ReceiptImage;
import com.lokoko.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptImageRepository extends JpaRepository<ReceiptImage, Long> {
    void deleteAllByReview(Review review);
}
