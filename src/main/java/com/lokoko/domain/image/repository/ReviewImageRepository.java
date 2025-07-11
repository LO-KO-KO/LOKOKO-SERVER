package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    // 리뷰 이미지가 참조하는 리뷰
    // 리뷰가 참조하는 상품
    // 대표 이미지(displayOrder = 0)만 조회
    @Query("""
                SELECT ri FROM ReviewImage ri
                JOIN FETCH ri.review r         
                JOIN FETCH r.product p       
                WHERE ri.displayOrder = 0      
            """)
    List<ReviewImage> findAllMainReviewImageWithReview();

    void deleteAllByReview(Review review);
}
