package com.lokoko.domain.review.dto.response;

import java.util.List;

public record ReviewReceiptResponse(
        List<ReceiptUrl> receiptUrl
) {
}
