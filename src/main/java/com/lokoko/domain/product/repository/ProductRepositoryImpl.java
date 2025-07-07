package com.lokoko.domain.product.repository;

import static com.lokoko.domain.product.entity.QProduct.product;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.entity.QReview;
import com.lokoko.domain.review.entity.enums.Rating;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

/**
 * 1단계 : 완전 일치 검색 - 모든 토큰을 연결한 문자열로 검색한다. 2단계 : 모든 토큰 포함(AND 검색) - 각 토큰이 모두 포함된 경우 3단계 : 주요 토큰 포함 - 첫 번째 토큰(일반적으로 브랜드명)
 * 과 마지막 토큰 포함하는 경우 4단계 : 일부 토큰 포함 (or 검색) - 하나라도 포함되면 조회
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProduct p = QProduct.product;
    private final QReview r = QReview.review;


    /**
     * 주어진 토큰 리스트를 기반으로 상품 검색 단계적으로 검색이 수행되고, 각 단계에서 결과가 존재하면 바로 반환
     *
     * @param tokens 형태소 분석기를 통해서 ㅁ나들어진 검색어 토큰 리스트
     * @return 검색 조건에 부합하는 Product List
     */
    @Override
    public Slice<Product> searchByTokens(List<String> tokens, Pageable pageable) {
        List<Product> allMatches;
        if (tokens.isEmpty()) {
            allMatches = List.of();
        } else {
            String fullKeyword = String.join("", tokens);
            List<Product> exactMatches = queryFactory
                    .selectFrom(product)
                    .where(product.searchToken.containsIgnoreCase(fullKeyword))
                    .fetch();
            if (!exactMatches.isEmpty()) {
                allMatches = exactMatches;
            } else {
                BooleanBuilder allTokensMatch = new BooleanBuilder();
                tokens.forEach(token ->
                        allTokensMatch.and(product.searchToken.containsIgnoreCase(token))
                );
                List<Product> allTokenMatches = queryFactory
                        .selectFrom(product)
                        .where(allTokensMatch)
                        .fetch();
                if (!allTokenMatches.isEmpty()) {
                    allMatches = allTokenMatches;
                } else if (tokens.size() >= 3) {
                    BooleanBuilder majorTokensMatch = new BooleanBuilder()
                            .and(product.searchToken.containsIgnoreCase(tokens.get(0)))
                            .and(product.searchToken.containsIgnoreCase(tokens.get(tokens.size() - 1)));
                    List<Product> majorMatches = queryFactory
                            .selectFrom(product)
                            .where(majorTokensMatch)
                            .fetch();
                    if (!majorMatches.isEmpty()) {
                        allMatches = majorMatches;
                    } else {
                        BooleanBuilder anyTokenMatch = new BooleanBuilder();
                        tokens.forEach(token ->
                                anyTokenMatch.or(product.searchToken.containsIgnoreCase(token))
                        );
                        allMatches = queryFactory
                                .selectFrom(product)
                                .where(anyTokenMatch)
                                .fetch();
                    }
                } else {
                    BooleanBuilder anyTokenMatch = new BooleanBuilder();
                    tokens.forEach(token ->
                            anyTokenMatch.or(product.searchToken.containsIgnoreCase(token))
                    );
                    allMatches = queryFactory
                            .selectFrom(product)
                            .where(anyTokenMatch)
                            .fetch();
                }
            }
        }
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        List<Product> content;
        boolean hasNext = false;

        if (offset >= allMatches.size()) {
            content = List.of();
        } else {
            int toIndex = Math.min(offset + limit, allMatches.size());
            content = allMatches.subList(offset, toIndex);
            hasNext = allMatches.size() > toIndex;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, Pageable pageable) {
        NumberExpression<Long> reviewCount = r.id.count();
        NumberExpression<Integer> ratingNum = new CaseBuilder()
                .when(r.rating.eq(Rating.ONE)).then(1)
                .when(r.rating.eq(Rating.TWO)).then(2)
                .when(r.rating.eq(Rating.THREE)).then(3)
                .when(r.rating.eq(Rating.FOUR)).then(4)
                .when(r.rating.eq(Rating.FIVE)).then(5)
                .otherwise(0);
        NumberExpression<Double> ratingAvgExpr = ratingNum.castToNum(Double.class).avg();

        List<Product> content = queryFactory
                .select(p)
                .from(p)
                .leftJoin(r).on(r.product.eq(p))
                .where(p.middleCategory.eq(category))
                .groupBy(p.id)
                .orderBy(
                        reviewCount.desc(),                         // 리뷰 수 내림차순
                        ratingAvgExpr.desc()                        // 평균 평점 내림차순
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        boolean hasNext = content.size() == pageable.getPageSize();

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<Product> findProductsByPopularityAndRating(
            MiddleCategory category,
            SubCategory subCategory,
            Pageable pageable
    ) {
        NumberExpression<Long> reviewCount = r.id.count();
        NumberExpression<Integer> ratingNum = new CaseBuilder()
                .when(r.rating.eq(Rating.ONE)).then(1)
                .when(r.rating.eq(Rating.TWO)).then(2)
                .when(r.rating.eq(Rating.THREE)).then(3)
                .when(r.rating.eq(Rating.FOUR)).then(4)
                .when(r.rating.eq(Rating.FIVE)).then(5)
                .otherwise(0);
        NumberExpression<Double> ratingAvgExpr = ratingNum.castToNum(Double.class).avg();

        BooleanExpression where = p.middleCategory.eq(category);
        if (subCategory != null) {
            where = where.and(p.subCategory.eq(subCategory));
        }

        List<Product> content = queryFactory
                .select(p)
                .from(p)
                .leftJoin(r).on(r.product.eq(p))
                .where(where)
                .groupBy(p.id)
                .orderBy(reviewCount.desc(), ratingAvgExpr.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        boolean hasNext = content.size() == pageable.getPageSize();
        return new SliceImpl<>(content, pageable, hasNext);
    }
}