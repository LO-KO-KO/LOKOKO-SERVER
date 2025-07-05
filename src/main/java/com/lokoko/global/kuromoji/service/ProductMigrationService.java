package com.lokoko.global.kuromoji.service;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductMigrationService {

    private final ProductRepository productRepository;
    private final KuromojiService kuromojiProcessor;

    @Transactional
    public void migrateSearchFields() {
        List<Product> allProducts = productRepository.findAll();

        for (Product product : allProducts) {
            String name = product.getProductName();
            String brand = product.getBrandName();

            List<String> tokens = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                tokens.addAll(kuromojiProcessor.tokenize(name));
            }

            if (brand != null && !brand.isBlank()) {
                tokens.addAll(kuromojiProcessor.tokenize(brand));
            }

            String joinedTokens = String.join(" ", tokens);
            product.updateSearchToken(joinedTokens);
        }
    }

}