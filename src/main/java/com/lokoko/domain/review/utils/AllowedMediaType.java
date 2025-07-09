package com.lokoko.domain.review.utils;

import java.util.Set;

public final class AllowedMediaType {
    private AllowedMediaType() {
    }

    public static final Set<String> ALLOWED_MEDIA_TYPES = Set.of(
            "image/jpg",
            "image/jpeg",
            "image/png",
            "image/webp",
            "video/mp4",
            "video/avi",
            "video/mkv",
            "video.quicktime"
    );
}