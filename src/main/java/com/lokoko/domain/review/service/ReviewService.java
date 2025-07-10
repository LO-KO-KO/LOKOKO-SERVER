package com.lokoko.domain.review.service;

import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

import com.lokoko.domain.review.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.review.dto.response.ReviewReceiptUrl;
import com.lokoko.domain.review.exception.ErrorMessage;
import com.lokoko.domain.review.exception.InvalidMediaTypeException;
import com.lokoko.global.common.service.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final S3Service s3Service;

    public ReviewReceiptResponse createReceiptPresignedUrl(ReviewReceiptRequest request) {

        String mediaType = request.mediaType();

        // "video/" 또는 "image/"로 시작하는지, 슬래시가 포함되어 있는지 검사
        if (!(mediaType.startsWith("video/") || mediaType.startsWith("image/")) || !mediaType.contains("/")) {
            throw new InvalidMediaTypeException(ErrorMessage.INVALID_MEDIA_TYPE_FORMAT);
        }

        //  허용된 mediaType인지 체크
        if (!ALLOWED_MEDIA_TYPES.contains(mediaType)) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }
        String presignedUrl = String.valueOf(s3Service.generatePresignedUrl(mediaType));
        return new ReviewReceiptResponse(List.of(new ReviewReceiptUrl(presignedUrl)));

    }
}


