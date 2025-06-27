package com.lokoko.domain.like.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
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

/**
 * 리뷰 좋아요
 */
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_review_like_review_user", columnNames = {"review_id", "user_id"})
}) // (리뷰, 유저) 에 대해 유니크 제약조건 설정하여 같은 유저가 여러번 좋아요 누를 수 없도록 설정.
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id") // foreign key
    private Review review; // 어떤 리뷰에 대한 좋아요인지

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id") //  foreign key
    private User user;  // 어떤 회원이 좋아요를 눌렀는지

    // 정적 팩토리 메소드
    public static ReviewLike createReviewLike(Review review, User user) {
        return ReviewLike.builder()
                .review(review)
                .user(user)
                .build();
    }

}
