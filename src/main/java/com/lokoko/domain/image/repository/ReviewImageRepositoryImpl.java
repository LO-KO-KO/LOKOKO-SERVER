package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.like.entity.QReviewLike;
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
    private static final QReviewImage reviewImage = QReviewImage.reviewImage;
    private static final QReview review = QReview.review;
    private static final QProduct product = QProduct.product;
    private static final QReviewLike reviewLike = QReviewLike.reviewLike;

    @Override
    public List<ReviewImage> findMainImageReviewSorted() {
        return queryFactory
                .selectFrom(reviewImage)
                .join(reviewImage.review, review).fetchJoin()
                .join(review.product, product).fetchJoin()
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                // 대표 이미지 조건: displayOrder == 0
                .where(reviewImage.displayOrder.eq(0))
                .groupBy(reviewImage.id, review.id, product.id, review.rating)
                // 정렬 조건: 좋아요 내림차순 → 평점 내림차순
                .orderBy(
                        reviewLike.count().desc(),
                        review.rating.desc()
                )
                .limit(4)
                .fetch();
    }
}