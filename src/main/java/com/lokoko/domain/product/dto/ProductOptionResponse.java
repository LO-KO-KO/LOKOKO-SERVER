package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.entity.ProductOption;

public record ProductOptionResponse(
        Long id,
        String optionName
) {
    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(option.getId(), option.getOptionName());
    }
}
