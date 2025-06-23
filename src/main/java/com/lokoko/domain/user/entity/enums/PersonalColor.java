package com.lokoko.domain.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PersonalColor {
    SPRING_WARM("봄 웜"),
    SUMMER_COOL("여름 쿨"),
    AUTUMN_WARM("가을 웜"),
    AUTUMN_COOL("가을 쿨"),
    WINTER_COOL("겨울 쿨"),
    NOT_SPECIFIED("모름");

    private final String displayName;
}
