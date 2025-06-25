package com.lokoko.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lokoko.domain.image.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
