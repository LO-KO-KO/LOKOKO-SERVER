package com.lokoko.global.common.service;

import com.lokoko.domain.image.exception.FileTypeNotSupportedException;import com.lokoko.domain.review.exception.ErrorMessage;
import com.lokoko.domain.review.exception.InvalidMediaTypeException;
import com.lokoko.global.common.dto.PresignedUrlResponse;
import com.lokoko.global.config.S3Config;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Config s3Config;

    public PresignedUrlResponse generatePresignedUrl(String fileType) {
        String uuid = UUID.randomUUID().toString();
        String extension = getExtensionFromMideaType(fileType);

        String key;

        if (fileType.startsWith("image")) {
            key = "image/" + uuid + extension;
        } else if (fileType.startsWith("video")) {
            key = "video/" + uuid + extension;
        } else {
            throw new FileTypeNotSupportedException();
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(key)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(builder -> builder
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
        );

        return new PresignedUrlResponse(presignedRequest.url().toString());
    }

    private String getExtensionFromMideaType(String mimeType) {
        int slashIndex = mimeType.lastIndexOf('/');
        if (slashIndex == -1 || slashIndex == mimeType.length() - 1) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }
        return mimeType.substring(slashIndex + 1);
    }
}
