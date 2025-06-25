package com.lokoko.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lokoko.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
