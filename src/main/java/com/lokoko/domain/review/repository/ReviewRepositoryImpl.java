package com.lokoko.domain.review.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.ImageReviewResponse;
import com.lokoko.domain.review.dto.VideoReviewResponse;
import com.lokoko.domain.review.entity.QReview;
import com.lokoko.domain.video.entity.QReviewVideo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;
    private final QProduct product = QProduct.product;
    private final QReviewVideo reviewVideo = QReviewVideo.reviewVideo;
    private final QReviewImage reviewImage = QReviewImage.reviewImage;

    @Override
    public Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                 SubCategory subCategory,
                                                                 Pageable pageable) {
        List<VideoReviewResponse> content = queryFactory
                .select(Projections.constructor(VideoReviewResponse.class,
                        review.id,
                        Expressions.constant(0),
                        product.brandName,
                        product.productName,
                        review.likeCount,
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .innerJoin(reviewVideo.review, review)
                .innerJoin(review.product, product)
                .where(
                        categoryCondition(middleCategory, subCategory)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        for (int i = 0; i < Math.min(content.size(), pageable.getPageSize()); i++) {
            VideoReviewResponse original = content.get(i);
            content.set(i, new VideoReviewResponse(
                    original.reviewId(),
                    (int) pageable.getOffset() + i + 1,
                    original.brandName(),
                    original.productName(),
                    original.likeCount(),
                    original.url()
            ));
        }

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                 Pageable pageable) {
        return findVideoReviewsByCategory(middleCategory, null, pageable);
    }


    @Override
    public Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory,
                                                                 SubCategory subCategory,
                                                                 Pageable pageable) {
        List<ImageReviewResponse> content = queryFactory
                .select(Projections.constructor(ImageReviewResponse.class,
                        review.id,
                        Expressions.constant(0),
                        product.brandName,
                        product.productName,
                        review.likeCount,
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .innerJoin(review.product, product)
                .where(
                        categoryCondition(middleCategory, subCategory)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        for (int i = 0; i < Math.min(content.size(), pageable.getPageSize()); i++) {
            ImageReviewResponse original = content.get(i);
            content.set(i, new ImageReviewResponse(
                    original.reviewId(),
                    (int) pageable.getOffset() + i + 1,
                    original.brandName(),
                    original.productName(),
                    original.likeCount(),
                    original.url()
            ));
        }

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }


    @Override
    public Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory,
                                                                 Pageable pageable) {
        return findImageReviewsByCategory(middleCategory, null, pageable);
    }


    private BooleanExpression categoryCondition(MiddleCategory middleCategory, SubCategory subCategory) {
        BooleanExpression condition = product.middleCategory.eq(middleCategory);

        if (subCategory != null) {
            condition = condition.and(product.subCategory.eq(subCategory));
        }
        return condition;
    }

    @Override
    public Slice<VideoReviewResponse> findVideoReviewsByKeyword(List<String> tokens, Pageable pageable) {
        List<VideoReviewResponse> content = getVideoReviewsByKeyword(tokens, pageable);
        updateVideoSequence(content, pageable);
        return createSlice(content, pageable);
    }

    @Override
    public Slice<ImageReviewResponse> findImageReviewsByKeyword(List<String> tokens, Pageable pageable) {
        List<ImageReviewResponse> content = getImageReviewsByKeyword(tokens, pageable);
        updateImageSequence(content, pageable);
        return createSlice(content, pageable);
    }

    private List<VideoReviewResponse> getVideoReviewsByKeyword(List<String> tokens, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(VideoReviewResponse.class,
                        review.id,
                        Expressions.constant(0),
                        product.brandName,
                        product.productName,
                        review.likeCount,
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .innerJoin(reviewVideo.review, review)
                .innerJoin(review.product, product)
                .where(keywordCondition(tokens))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private List<ImageReviewResponse> getImageReviewsByKeyword(List<String> tokens, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(ImageReviewResponse.class,
                        review.id,
                        Expressions.constant(0),
                        product.brandName,
                        product.productName,
                        review.likeCount,
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .innerJoin(review.product, product)
                .where(keywordCondition(tokens))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanBuilder keywordCondition(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }

        // 1단계: 완전 일치 검색
        String fullKeyword = String.join("", tokens);
        BooleanExpression exactMatch = product.searchToken.containsIgnoreCase(fullKeyword);

        // 2단계: 모든 토큰 포함 (AND 검색)
        BooleanBuilder allTokensMatch = new BooleanBuilder();
        tokens.forEach(token ->
                allTokensMatch.and(product.searchToken.containsIgnoreCase(token))
        );

        // 3단계: 주요 토큰 포함 (첫 번째와 마지막 토큰)
        BooleanExpression majorTokensMatch = null;
        if (tokens.size() >= 3) {
            majorTokensMatch = product.searchToken.containsIgnoreCase(tokens.get(0))
                    .and(product.searchToken.containsIgnoreCase(tokens.get(tokens.size() - 1)));
        }

        // 4단계: 일부 토큰 포함 (OR 검색)
        BooleanBuilder anyTokenMatch = new BooleanBuilder();
        tokens.forEach(token ->
                anyTokenMatch.or(product.searchToken.containsIgnoreCase(token))
        );

        // 모든 검색 조건을 OR로 연결
        BooleanBuilder finalCondition = new BooleanBuilder();
        finalCondition.or(exactMatch);
        finalCondition.or(allTokensMatch);
        if (majorTokensMatch != null) {
            finalCondition.or(majorTokensMatch);
        }
        finalCondition.or(anyTokenMatch);

        return finalCondition;
    }

    private void updateVideoSequence(List<VideoReviewResponse> content, Pageable pageable) {
        for (int i = 0; i < Math.min(content.size(), pageable.getPageSize()); i++) {
            VideoReviewResponse original = content.get(i);
            VideoReviewResponse updated = new VideoReviewResponse(
                    original.reviewId(),
                    (int) pageable.getOffset() + i + 1,
                    original.brandName(),
                    original.productName(),
                    original.likeCount(),
                    original.url()
            );
            content.set(i, updated);
        }
    }

    private void updateImageSequence(List<ImageReviewResponse> content, Pageable pageable) {
        for (int i = 0; i < Math.min(content.size(), pageable.getPageSize()); i++) {
            ImageReviewResponse original = content.get(i);
            ImageReviewResponse updated = new ImageReviewResponse(
                    original.reviewId(),
                    (int) pageable.getOffset() + i + 1,
                    original.brandName(),
                    original.productName(),
                    original.likeCount(),
                    original.url()
            );
            content.set(i, updated);
        }
    }

    private <T> Slice<T> createSlice(List<T> content, Pageable pageable) {
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

}

