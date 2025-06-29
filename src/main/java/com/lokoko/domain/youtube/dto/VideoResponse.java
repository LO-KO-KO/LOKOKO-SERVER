package com.lokoko.domain.youtube.dto;

import com.lokoko.domain.video.entity.YoutubeVideo;
import java.time.LocalDateTime;

public record VideoResponse(
        Long id,
        String topic,
        String title,
        String url,
        Integer popularity,
        Long viewCount,
        LocalDateTime uploadedAt
) {
    public static VideoResponse from(YoutubeVideo video) {
        return new VideoResponse(
                video.getId(),
                video.getTopic(),
                video.getTitle(),
                video.getUrl(),
                video.getPopularity(),
                video.getViewCount(),
                video.getUploadedAt()
        );
    }
}
