package com.lokoko.domain.youtube.service;

import com.google.api.services.youtube.YouTube;
import com.lokoko.domain.video.entity.YoutubeVideo;
import com.lokoko.domain.youtube.exception.YoutubeApiException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularVideoCrawler {
    private final YouTube youtube;
    private final String apiKey;

    public List<YoutubeVideo> crawl(List<String> topics) {
        List<YoutubeVideo> result = new ArrayList<>();

        for (String topic : topics) {
            try {
                List<String> ids = searchVideoIds(topic);
                result.addAll(fetchVideoDetails(topic, ids));
            } catch (IOException e) {
                log.error("유튜브 API 에러 ({}): {}", topic, e.getMessage(), e);
                throw new YoutubeApiException();
            }
        }
        return result;
    }

    private List<String> searchVideoIds(String topic) throws IOException {
        YouTube.Search.List search = youtube.search().list(List.of("id"));
        search.setKey(apiKey)
                .setQ(topic)
                .setType(List.of("video"))
                .setOrder("viewCount")
                .setPublishedAfter("2024-01-01T00:00:00Z")
                .setMaxResults(10L);

        return search.execute()
                .getItems()
                .stream()
                .map(item -> item.getId().getVideoId())
                .toList();
    }

    private List<YoutubeVideo> fetchVideoDetails(String topic, List<String> ids) throws IOException {
        if (ids.isEmpty()) {
            return List.of();
        }

        YouTube.Videos.List list = youtube.videos()
                .list(List.of("snippet", "statistics"));

        list.setKey(apiKey)
                .setId(ids);

        return list.execute()
                .getItems()
                .stream()
                .map(item -> {
                    Instant instant = Instant.ofEpochMilli(item.getSnippet().getPublishedAt().getValue());
                    LocalDateTime uploadedAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                    return YoutubeVideo.of(
                            topic,
                            item.getSnippet().getTitle(),
                            "https://www.youtube.com/watch?v=" + item.getId(),
                            item.getStatistics().getViewCount().intValue(),
                            item.getStatistics().getViewCount().longValue(),
                            uploadedAt
                    );
                })
                .toList();
    }
}
