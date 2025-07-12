package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.review.entity.QReview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewImageRepositoryImpl implements ReviewImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReviewImage reviewImage = QReviewImage.reviewImage;
    private final QReview review = QReview.review;
    private final QProduct product = QProduct.product;

    @Override
    public List<ReviewImage> findMainImageReviewSorted() {
        return queryFactory
                .selectFrom(reviewImage)
                .join(reviewImage.review, review).fetchJoin()
                .join(review.product, product).fetchJoin()
                // 대표 이미지 조건: displayOrder == 0
                .where(reviewImage.displayOrder.eq(0))
                // 정렬 조건: 좋아요 내림차순 → 평점 내림차순
                .orderBy(
                        review.likeCount.desc(),
                        review.rating.desc()
                )
                .limit(4)
                .fetch();
    }
}