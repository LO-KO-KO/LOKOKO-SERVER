package com.lokoko.domain.like.service;

import com.lokoko.domain.like.entity.ReviewLike;
import com.lokoko.domain.like.repository.ReviewLikeRepository;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.repository.ReviewRepository;
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
public class ReviewLikeService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public long toggleReviewLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Optional<ReviewLike> existing = reviewLikeRepository
                .findByReviewIdAndUserId(reviewId, userId);
        if (existing.isPresent()) {
            reviewLikeRepository.delete(existing.get());
        } else {
            reviewLikeRepository.save(ReviewLike.of(review, user));
        }

        return reviewLikeRepository.countByReviewId(reviewId);
    }
}
