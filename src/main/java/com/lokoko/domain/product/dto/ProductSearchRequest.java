package com.lokoko.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ProductSearchRequest(

        @NotBlank(message = "검색어는 최소 한 글자 이상이어야 합니다.")
        @Size(max = 20, message = "검색어는 20자를 넘을 수 없습니다.")
        String keyword

) {
}
