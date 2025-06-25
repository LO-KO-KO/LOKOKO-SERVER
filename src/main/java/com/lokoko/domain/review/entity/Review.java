package com.lokoko.domain.review.entity;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User author; // 리뷰 작성자 foreign key 매핑

	// Product 엔티티가 만들어지고 난 후, 주석 해제 해야합니다.
	// @ManyToOne
	// @JoinColumn(name = "product_id")
	// private Product product;

	private String positiveContent; // 긍정 리뷰 내용

	private String negativeContent; // 부정 리뷰 내용

	private int likeCount; // 좋아요 수

	public static Review createReview(User user, String positive, String negative) {
		return Review.builder()
			.author(user)
			.positiveContent(positive)
			.negativeContent(negative)
			.build();
	}

	// 긍정 리뷰 내용 수정
	public void changePositiveContent(String content) {
		this.positiveContent = content;
	}

	// 부정 리뷰 내용 수정
	public void changeNegativeContent(String content) {
		this.negativeContent = content;
	}



}
