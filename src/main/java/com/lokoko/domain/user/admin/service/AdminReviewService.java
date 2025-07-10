package com.lokoko.domain.user.admin.service;

import com.lokoko.domain.image.repository.ReceiptImageRepository;
import com.lokoko.domain.image.repository.ReviewImageRepository;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.domain.video.repository.ReviewVideoRepository;
import com.lokoko.global.utils.AdminValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReceiptImageRepository receiptImageRepository;

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        AdminValidator.validateUserRole(user);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        deleteAllMediaOfReview(review);
        reviewRepository.delete(review);
    }

    private void deleteAllMediaOfReview(Review review) {
        receiptImageRepository.deleteAllByReview(review);
        reviewImageRepository.deleteAllByReview(review);
        reviewVideoRepository.deleteAllByReview(review);
    }
}
