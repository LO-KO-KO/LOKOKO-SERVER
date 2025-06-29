package com.lokoko.domain.youtube.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeApiService {
    private static final int MAX_VIDEO_COUNT = 5;
    private static final List<String> AD_KEYWORDS = Arrays.asList(
            "유료 광고", "sponsored"
    );
    private final YouTube youtube;
    private final String apiKey;

    public List<String> searchReviewVideos(String productName) {
        List<String> videoUrls = new ArrayList<>();
        String query = productName + " 리뷰";

        try {
            YouTube.Search.List search = youtube.search().list(Collections.singletonList("snippet"));
            search.setKey(apiKey);
            search.setQ(query);
            search.setType(Collections.singletonList("video"));
            search.setMaxResults(10L);
            search.setOrder("relevance");
            search.setSafeSearch("none");

            SearchListResponse response = search.execute();
            List<SearchResult> items = response.getItems();

            for (SearchResult item : items) {
                if (videoUrls.size() >= MAX_VIDEO_COUNT) {
                    break;
                }
                String videoId = item.getId().getVideoId();
                String title = item.getSnippet().getTitle().toLowerCase();
                if (isValidVideo(title, productName.toLowerCase())) {
                    videoUrls.add("https://www.youtube.com/watch?v=" + videoId);
                }
            }
        } catch (Exception e) {
            log.error("YouTube API 호출 실패: {}", e.getMessage());
        }
        return videoUrls;
    }

    private boolean isValidVideo(String title, String productNameLower) {
        boolean isAd = AD_KEYWORDS.stream().anyMatch(title::contains);
        String[] keywords = productNameLower.split(" ");
        long matchCount = Arrays.stream(keywords)
                .filter(title::contains)
                .count();

        return !isAd && matchCount >= 2;
    }
}
