package com.lokoko.domain.product.repository;

import static com.lokoko.domain.product.entity.QProduct.product;

import com.lokoko.domain.product.entity.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 1단계 : 완전 일치 검색 - 모든 토큰을 연결한 문자열로 검색한다. 2단계 : 모든 토큰 포함(AND 검색) - 각 토큰이 모두 포함된 경우 3단계 : 주요 토큰 포함 - 첫 번째 토큰(일반적으로 브랜드명)
 * 과 마지막 토큰 포함하는 경우 4단계 : 일부 토큰 포함 (or 검색) - 하나라도 포함되면 조회
 */
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 주어진 토큰 리스트를 기반으로 상품 검색 단계적으로 검색이 수행되고, 각 단계에서 결과가 존재하면 바로 반환
     *
     * @param tokens 형태소 분석기를 통해서 ㅁ나들어진 검색어 토큰 리스트
     * @return 검색 조건에 부합하는 Product List
     */
    @Override
    public List<Product> searchByTokens(List<String> tokens) {

        if (tokens.isEmpty()) {
            return List.of();
        }

        String fullKeyword = String.join("", tokens);
        List<Product> exactMatches = queryFactory
                .selectFrom(product)
                .where(product.searchToken.containsIgnoreCase(fullKeyword))
                .fetch();

        if (!exactMatches.isEmpty()) {
            return exactMatches;
        }

        BooleanBuilder allTokensMatch = new BooleanBuilder();
        tokens.forEach(token ->
                allTokensMatch.and(product.searchToken.containsIgnoreCase(token))
        );

        List<Product> allTokenMatches = queryFactory
                .selectFrom(product)
                .where(allTokensMatch)
                .fetch();

        if (!allTokenMatches.isEmpty()) {
            return allTokenMatches;
        }

        if (tokens.size() >= 3) {
            BooleanBuilder majorTokensMatch = new BooleanBuilder();

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