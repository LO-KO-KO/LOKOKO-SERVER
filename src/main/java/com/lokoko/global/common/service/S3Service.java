package com.lokoko.global.common.service;

import com.lokoko.domain.image.exception.FileTypeNotSupportedException;
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
        String key;

        if (fileType.startsWith("image")) {
            key = "image/" + uuid + ".jpg";
        } else if (fileType.startsWith("video")) {
            key = "video/" + uuid + ".mp4";
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
}
