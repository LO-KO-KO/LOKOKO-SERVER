package com.lokoko.domain.review.entity.enums;

import com.lokoko.domain.review.exception.RatingNotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rating {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    public static Rating fromValue(int dbData) {
        return Arrays.stream(values())
                .filter(r -> r.value == dbData)
                .findFirst()
                .orElseThrow(RatingNotFoundException::new);
    }
}
