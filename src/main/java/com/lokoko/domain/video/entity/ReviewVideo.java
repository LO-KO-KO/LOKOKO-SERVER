package com.lokoko.domain.video.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.review.entity.Review;
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
public class ReviewVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_video_id")
    private Long id;

    @Embedded
    private MediaFile mediaFile; // 파일 이름, 파일 경로 포함

    private int displayOrder; // 배치순서

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review; // 동영상이 어떤 리뷰에 대한 것인지 (외래키)

    public static ReviewVideo createReviewVideo(String fileName,
                                                String fileUrl, int displayOrder, Review review) {
        return ReviewVideo.builder()
                .mediaFile(MediaFile.of(fileName, fileUrl))
                .displayOrder(displayOrder)
                .review(review)
                .build();
    }
}
