package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long>, ReviewImageRepositoryCustom {

    @Query("""
                SELECT ri FROM ReviewImage ri
                JOIN FETCH ri.review r         
                JOIN FETCH r.product p       
                WHERE ri.displayOrder = 0      
            """)
    List<ReviewImage> findAllMainReviewImageWithReview();

    void deleteAllByReview(Review review);
}
