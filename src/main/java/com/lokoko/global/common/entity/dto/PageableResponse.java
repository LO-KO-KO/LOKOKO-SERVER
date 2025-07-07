package com.lokoko.global.common.entity.dto;

import org.springframework.data.domain.Slice;

public record PageableResponse(

        int pageNumber,
        int pageSize,
        int numberOfElements,
        boolean isLast
) {

    public static PageableResponse from(Slice<?> slice) {
        return new PageableResponse(
                slice.getNumber(),
                slice.getSize(),
                slice.getNumberOfElements(),
                slice.isLast()
        );
    }

}