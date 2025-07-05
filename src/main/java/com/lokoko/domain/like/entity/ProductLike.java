package com.lokoko.domain.like.entity;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.user.entity.User;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 상품 좋아요
 */
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_like_user", columnNames = {"product_id", "user_id"})
})
public class ProductLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_like_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id") // foreign key
    private Product product; // 어떤 상품에 대한 좋아요인지

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id") //  foreign key
    private User user;  // 어떤 회원이 좋아요를 눌렀는지

    // 정적 팩토리 메소드
    public static ProductLike createProductLike(Product product, User user) {
        return ProductLike.builder()
                .product(product)
                .user(user)
                .build();
    }

}
