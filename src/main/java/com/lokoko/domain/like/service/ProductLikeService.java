package com.lokoko.domain.like.service;

import com.lokoko.domain.like.entity.ProductLike;
import com.lokoko.domain.like.repository.ProductLikeRepository;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductLikeService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductLikeRepository productLikeRepository;

    @Transactional
    public void toggleProductLike(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        processProductLike(product, user);
    }

    public long getProductLikeCount(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException();
        }

        return productLikeRepository.countByProductId(productId);
    }

    public void processProductLike(Product product, User user) {
        Optional<ProductLike> existingLike =
                productLikeRepository.findByProductIdAndUserId(product.getId(), user.getId());

        if (existingLike.isPresent()) {
            productLikeRepository.delete(existingLike.get());
        } else {
            ProductLike newLike = ProductLike.createProductLike(product, user);
            productLikeRepository.save(newLike);
        }
    }
}
