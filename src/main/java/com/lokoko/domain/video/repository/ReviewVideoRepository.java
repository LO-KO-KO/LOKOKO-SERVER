package com.lokoko.domain.video.repository;

import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.video.entity.ReviewVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewVideoRepository extends JpaRepository<ReviewVideo, Long> ,ReviewVideoRepositoryCustom{

    // 리뷰 비디오ㅓ 참조하는 리뷰
    // 리뷰가 참조하는 상품
    // 대표 영상(displayOrder = 0)만 조회
    @Query("""
                SELECT ri FROM ReviewVideo ri
                JOIN FETCH ri.review r         
                JOIN FETCH r.product p       
                WHERE ri.displayOrder = 0      
            """)
    List<ReviewVideo> findAllMainReviewVideoWithReview();

    void deleteAllByReview(Review review);
}
