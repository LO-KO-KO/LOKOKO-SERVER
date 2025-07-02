package com.lokoko.domain.video.entity;

import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YoutubeVideo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "youtube_video_id")
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Integer popularity;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public static YoutubeVideo of(String topic, String title, String url, Integer popularity, Long viewCount,
                                  LocalDateTime uploadedAt) {
        YoutubeVideo video = new YoutubeVideo();
        video.topic = topic;
        video.title = title;
        video.url = url;
        video.popularity = popularity;
        video.viewCount = viewCount;
        video.uploadedAt = uploadedAt;
        return video;
    }

    public void updatePopularity(Integer popularity) {
        this.popularity = popularity;
    }
}
