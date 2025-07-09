package com.lokoko.domain.review.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReviewReceiptRequest(
        @NotNull String mediaType
) {
}
