package com.lokoko.domain.review.service;

import com.lokoko.domain.review.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.dto.response.ReceiptUrl;
import com.lokoko.domain.review.dto.response.ReviewReceiptResponse;
import com.lokoko.global.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final S3Service s3Service;

    public ReviewReceiptResponse createReceiptPresignedUrl(ReviewReceiptRequest request) {
        String presignedUrl = String.valueOf(s3Service.generatePresignedUrl(request.mediaType()));

        return new ReviewReceiptResponse(List.of(new ReceiptUrl(presignedUrl)));
    }
}
