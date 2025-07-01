package com.lokoko.domain.youtube.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    REVIEW_CRAWL_SUCCESS("유튜브 리뷰영상 크롤링에 성공했습니다."),
    POPULAR_TRENDS_CRAWL_SUCCESS("인기 뷰티 트렌드 영상 크롤링에 성공했습니다."),

    POPULAR_TRENDS_GET_SUCCESS("인기 뷰티 트렌드 영상 조회에 성공했습니다.");

    private final String message;
}
