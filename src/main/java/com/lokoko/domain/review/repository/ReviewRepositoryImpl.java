package com.lokoko.domain.review.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.like.entity.QReviewLike;
import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.dto.response.ImageReviewResponse;
import com.lokoko.domain.review.dto.response.VideoReviewResponse;
import com.lokoko.domain.review.entity.QReview;
import com.lokoko.domain.video.entity.QReviewVideo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
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
    private final QReviewLike reviewLike = QReviewLike.reviewLike;


    @Override
    public Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                 SubCategory subCategory,
                                                                 Pageable pageable) {
        List<VideoReviewResponse> content = queryFactory
                .select(Projections.constructor(VideoReviewResponse.class,
                        review.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .innerJoin(reviewVideo.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(
                        categoryCondition(middleCategory, subCategory)
                )
                .groupBy(review.id, product.brandName, product.productName, reviewVideo.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

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
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(
                        categoryCondition(middleCategory, subCategory),
                        reviewImage.isMain.eq(true)
                )
                .groupBy(review.id, product.brandName, product.productName, reviewImage.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

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
        return createSlice(content, pageable);
    }

    @Override
    public Slice<ImageReviewResponse> findImageReviewsByKeyword(List<String> tokens, Pageable pageable) {
        List<ImageReviewResponse> content = getImageReviewsByKeyword(tokens, pageable);
        return createSlice(content, pageable);
    }

    private List<VideoReviewResponse> getVideoReviewsByKeyword(List<String> tokens, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(VideoReviewResponse.class,
                        review.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .innerJoin(reviewVideo.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(keywordCondition(tokens))
                .groupBy(review.id, product.brandName, product.productName, reviewVideo.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private List<ImageReviewResponse> getImageReviewsByKeyword(List<String> tokens, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(ImageReviewResponse.class,
                        review.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(keywordCondition(tokens),
                        reviewImage.isMain.eq(true))
                .groupBy(review.id, product.brandName, product.productName, reviewImage.mediaFile.fileUrl)
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
        BooleanBuilder exactMatch = new BooleanBuilder()
                .and(product.searchToken.containsIgnoreCase(fullKeyword));

        // 완전 일치 결과가 있는지 확인
        long exactMatchCount = queryFactory
                .select(review.count())
                .from(review)
                .innerJoin(review.product, product)
                .where(exactMatch)
                .fetchOne();

        if (exactMatchCount > 0) {
            return exactMatch;
        }

        // 2단계: 모든 토큰 포함 (AND 검색)
        BooleanBuilder allTokensMatch = new BooleanBuilder();
        tokens.forEach(token ->
                allTokensMatch.and(product.searchToken.containsIgnoreCase(token))
        );

        long allTokensCount = queryFactory
                .select(review.count())
                .from(review)
                .innerJoin(review.product, product)
                .where(allTokensMatch)
                .fetchOne();

        if (allTokensCount > 0) {
            return allTokensMatch;
        }

        // 3단계: 주요 토큰 포함 (첫 번째와 마지막 토큰)
        if (tokens.size() >= 3) {
            BooleanBuilder majorTokensMatch = new BooleanBuilder()
                    .and(product.searchToken.containsIgnoreCase(tokens.get(0)))
                    .and(product.searchToken.containsIgnoreCase(tokens.get(tokens.size() - 1)));

            long majorTokensCount = queryFactory
                    .select(review.count())
                    .from(review)
                    .innerJoin(review.product, product)
                    .where(majorTokensMatch)
                    .fetchOne();

            if (majorTokensCount > 0) {
                return majorTokensMatch;
            }
        }

        // 4단계: 일부 토큰 포함 (OR 검색)
        BooleanBuilder anyTokenMatch = new BooleanBuilder();
        tokens.forEach(token ->
                anyTokenMatch.or(product.searchToken.containsIgnoreCase(token))
        );

        return anyTokenMatch;
    }


    private <T> Slice<T> createSlice(List<T> content, Pageable pageable) {
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<RatingCount> countByProductIdsAndRating(List<Long> productIds) {
        List<Tuple> tuples = queryFactory
                .select(
                        product.id,
                        review.rating,
                        review.id.count()
                )
                .from(review)
                .join(review.product, product)
                .where(product.id.in(productIds))
                .groupBy(product.id, review.rating)
                .fetch();

        return tuples.stream()
                .map(t -> new RatingCount(
                        t.get(product.id),
                        t.get(review.rating),
                        t.get(review.id.count())
                ))
                .collect(Collectors.toList());
    }

}

