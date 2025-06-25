package com.lokoko.domain.image.entity;

import com.lokoko.domain.review.entity.Review;

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
public class ReceiptImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fileName; // 영수증 이미지 파일 이름

	private String fileUrl; // S3 상 영수증 이미지 파일 경로

	private int displayOrder; // 배치순서

	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review; // 영수증이 어떤 리뷰에 대한 것인지 (외래키)

	public static ReceiptImage createReceiptImage(String fileName,
		String fileUrl, int displayOrder, Review review) {
		return ReceiptImage.builder()
			.fileName(fileName)
			.fileUrl(fileUrl)
			.displayOrder(displayOrder)
			.review(review)
			.build();
	}


}
