package com.lokoko.domain.product.repository;

import com.lokoko.domain.product.entity.Product;
import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchByTokens(List<String> tokens);
}
