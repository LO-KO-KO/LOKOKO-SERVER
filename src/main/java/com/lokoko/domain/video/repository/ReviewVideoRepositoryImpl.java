package com.lokoko.domain.video.repository;

import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.review.entity.QReview;
import com.lokoko.domain.video.entity.QReviewVideo;
import com.lokoko.domain.video.entity.ReviewVideo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewVideoRepositoryImpl implements ReviewVideoRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private static final QReviewVideo reviewVideo = QReviewVideo.reviewVideo;
    private static final QReview review = QReview.review;
    private static final QProduct product = QProduct.product;

    @Override
    public List<ReviewVideo> findMainVideoReviewSorted() {
        return queryFactory
                .selectFrom(reviewVideo)
                .join(reviewVideo.review, review).fetchJoin()
                .join(review.product, product).fetchJoin()
                // 대표 이미지 조건: displayOrder == 0
                .where(reviewVideo.displayOrder.eq(0))
                // 정렬 조건: 좋아요 내림차순 → 평점 내림차순
                .orderBy(
                        review.likeCount.desc(),
                        review.rating.desc()
                )
                .limit(4)
                .fetch();
    }
}
