package com.lokoko.domain.like.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "REVIEW LIKE")
@RestController
@RequestMapping("/api/likes/reviews/{reviewId}")
@RequiredArgsConstructor
public class ReviewLikeController {
}
