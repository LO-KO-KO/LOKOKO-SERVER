package com.lokoko.domain.product.controller;


import com.lokoko.domain.product.controller.enums.ResponseMessage;
import com.lokoko.domain.product.dto.CrawlRequest;
import com.lokoko.domain.product.dto.CrawlResponse;
import com.lokoko.domain.product.service.ProductCrawlingService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductCrawlingService productCrawlingService;

    @PostMapping("/crawl")
    public ApiResponse<CrawlResponse> crawl(@RequestBody CrawlRequest request) {
        productCrawlingService.scrapeByCategory(request.mainCategory(), request.middleCategory());

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CRAWL_SUCCESS.getMessage(),
                CrawlResponse.of(request.mainCategory(), request.middleCategory()));
    }
}
