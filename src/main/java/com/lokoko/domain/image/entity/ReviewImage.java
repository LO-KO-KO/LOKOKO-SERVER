package com.lokoko.domain.image.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.review.entity.Review;
import com.lokoko.global.common.entity.BaseEntity;
import com.lokoko.global.common.entity.MediaFile;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @Embedded
    private MediaFile mediaFile; // 파일 이름, 파일 경로 포함하는 값 타임 객체

    private int displayOrder; // 배치순서

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review; // 이미지가 어떤 리뷰에 대한 것인지 (외래키)

    public static ReviewImage createReviewImage(MediaFile mediaFile, int displayOrder, Review review) {
        return ReviewImage.builder()
                .mediaFile(mediaFile)
                .displayOrder(displayOrder)
                .review(review)
                .build();
    }

    private boolean isMain;


}
