package com.lokoko.domain.review.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReviewRequest(
        @NotNull Long productOptionId,
        @NotNull Integer rating,
        @NotNull @Size(max = 500) String positiveComment,
        @NotNull @Size(max = 500) String negativeComment,
        List<String> mediaUrl,
        List<String> receiptUrl
) {
}
