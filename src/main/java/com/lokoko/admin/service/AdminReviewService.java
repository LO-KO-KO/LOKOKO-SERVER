package com.lokoko.admin.service;

import com.lokoko.domain.image.repository.ReceiptImageRepository;
import com.lokoko.domain.image.repository.ReviewImageRepository;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.domain.video.repository.ReviewVideoRepository;
import com.lokoko.global.auth.exception.AdminPermissionRequiredException;
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

        validateUserRole(user);

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

    private static void validateUserRole(User user) {
        if (!(user.getRole() == Role.ADMIN)) {
            throw new AdminPermissionRequiredException();
        }
    }
}
