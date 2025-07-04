package com.lokoko.domain.product.repository;

import static com.lokoko.domain.product.entity.QProduct.product;

import com.lokoko.domain.product.entity.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> searchByTokens(List<String> tokens) {

        if (tokens.isEmpty()) {
            return List.of();
        }

        // 1단계: 완전 일치 검색
        String fullKeyword = String.join("", tokens);
        List<Product> exactMatches = queryFactory
                .selectFrom(product)
                .where(product.searchToken.containsIgnoreCase(fullKeyword))
                .fetch();

        if (!exactMatches.isEmpty()) {
            return exactMatches; // 완전 일치 결과가 있으면 바로 반환
        }

        // 2단계: 모든 토큰 포함 검색 (AND match)
        BooleanBuilder allTokensMatch = new BooleanBuilder();
        tokens.forEach(token ->
                allTokensMatch.and(product.searchToken.containsIgnoreCase(token))
        );

        List<Product> allTokenMatches = queryFactory
                .selectFrom(product)
                .where(allTokensMatch)
                .fetch();

        if (!allTokenMatches.isEmpty()) {
            return allTokenMatches; // 모든 토큰 일치 결과가 있으면 반환
        }

        // 3단계: 주요 토큰만 검색 (브랜드명이나 상품명)
        // 토큰이 3개 이상인 경우만 적용
        if (tokens.size() >= 3) {
            BooleanBuilder majorTokensMatch = new BooleanBuilder();
            // 첫 번째 토큰(보통 브랜드명)과 마지막 토큰 조합
            majorTokensMatch.and(product.searchToken.containsIgnoreCase(tokens.get(0)))
                    .and(product.searchToken.containsIgnoreCase(tokens.get(tokens.size() - 1)));

            List<Product> majorMatches = queryFactory
                    .selectFrom(product)
                    .where(majorTokensMatch)
                    .fetch();

            if (!majorMatches.isEmpty()) {
                return majorMatches;
            }
        }

        // 4단계: 일부 토큰 검색 (OR match)
        BooleanBuilder anyTokenMatch = new BooleanBuilder();
        tokens.forEach(token ->
                anyTokenMatch.or(product.searchToken.containsIgnoreCase(token))
        );

        return queryFactory
                .selectFrom(product)
                .where(anyTokenMatch)
                .fetch();
    }
}