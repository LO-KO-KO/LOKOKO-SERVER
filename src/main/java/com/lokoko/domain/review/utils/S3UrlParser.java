package com.lokoko.domain.review.utils;

import com.lokoko.global.common.entity.MediaFile;

import java.net.URI;
import java.nio.file.Paths;

public class S3UrlParser {

    public static MediaFile parsePresignedUrl(String presignedUrl) {
        try {
            URI uri = new URI(presignedUrl);
            String path = uri.getPath();
            String fileName = Paths.get(path).getFileName().toString();
            String fileUrl = uri.getScheme() + "://" + uri.getHost() + path;

            return MediaFile.of(fileName, fileUrl);
        } catch (Exception e) {
            throw new RuntimeException("Presigned URL 파싱 실패: " + presignedUrl, e);
        }
    }
}
