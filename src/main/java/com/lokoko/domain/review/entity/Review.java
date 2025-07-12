package com.lokoko.domain.review.entity;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.ProductOption;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.user.entity.User;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author; // 리뷰 작성자 foreign key 매핑

    @Column(nullable = false, length = 100)
    private String productInfo;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 1500)
    private String positiveContent; // 긍정 리뷰 내용

    @Column(nullable = false, length = 1500)
    private String negativeContent; // 부정 리뷰 내용

    @Column(nullable = false)
    private int likeCount; // 좋아요 수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rating rating;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    // 긍정 리뷰 내용 수정
    public void changePositiveContent(String content) {
        this.positiveContent = content;
    }

    // 부정 리뷰 내용 수정
    public void changeNegativeContent(String content) {
        this.negativeContent = content;
    }
}
