package com.lokoko.domain.youtube.controller;

import com.lokoko.domain.product.controller.enums.ResponseMessage;
import com.lokoko.domain.product.dto.CrawlResponse;
import com.lokoko.domain.youtube.service.YoutubeCrawlerService;
import com.lokoko.global.common.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/youtube")
public class YoutubeController {
    private final YoutubeCrawlerService youtubeCrawlerService;

    @PostMapping("/{productId}/crawl")
    public ApiResponse<CrawlResponse> crawl(@PathVariable Long productId) {
        List<String> videoUrls = youtubeCrawlerService.crawlAndStoreReviews(productId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CRAWL_SUCCESS.getMessage(),
                CrawlResponse.of(videoUrls));
    }
}
