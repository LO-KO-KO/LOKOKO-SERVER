package com.lokoko.domain.youtube.controller;

import com.lokoko.domain.product.dto.CrawlResponse;
import com.lokoko.domain.youtube.controller.enums.ResponseMessage;
import com.lokoko.domain.youtube.dto.VideoResponse;
import com.lokoko.domain.youtube.service.YoutubeReviewCrawler;
import com.lokoko.domain.youtube.service.YoutubeTrendService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "YOUTUBE")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/youtube")
public class YoutubeController {
    private final YoutubeReviewCrawler youtubeReviewCrawler;
    private final YoutubeTrendService youtubeTrendService;

    @Hidden
    @Operation(summary = "유튜브 리뷰 크롤링 (SERVER ONLY)")
    @PostMapping("/{productId}/crawl")
    public ApiResponse<CrawlResponse> crawl(@PathVariable Long productId) {
        List<String> videoUrls = youtubeReviewCrawler.crawlAndStoreReviews(productId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_CRAWL_SUCCESS.getMessage(),
                CrawlResponse.of(videoUrls));
    }

    @Hidden
    @Operation(summary = "인기 뷰티 트렌드 영상 크롤링 (SERVER ONLY)")
    @PostMapping("/trends/crawl")
    public ApiResponse<Void> crawlPopularTrends() {
        youtubeTrendService.crawlPopularBeautyVideos();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.POPULAR_TRENDS_CRAWL_SUCCESS.getMessage());
    }

    @Operation(summary = "인기 뷰티 트렌드 영상 조회")
    @GetMapping("/trends")
    public ApiResponse<List<VideoResponse>> getPopularTrends() {
        List<VideoResponse> videos = youtubeTrendService.findAll();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.POPULAR_TRENDS_GET_SUCCESS.getMessage(), videos);
    }
}
