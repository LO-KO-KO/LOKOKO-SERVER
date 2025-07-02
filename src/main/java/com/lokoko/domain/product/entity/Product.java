package com.lokoko.domain.product.entity;

import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private long normalPrice;

    @Column(nullable = false)
    private String brandName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String productKoreanName;

    @Column(nullable = false)
    private String shippingInfo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String productDetail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String ingredients;

    private String unit;

    @Column(columnDefinition = "TEXT")
    private String youtubeUrl;

    @Column(nullable = false)
    private String oliveYoungUrl;

    @Column
    private String qoo10Url;

    @Enumerated(EnumType.STRING)
    @Column
    private Tag tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MainCategory mainCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MiddleCategory middleCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubCategory subCategory;

    public void updateYoutubeUrls(List<String> urls) {
        this.youtubeUrl = String.join(",", urls);
    }
}
