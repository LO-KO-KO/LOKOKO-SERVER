package com.lokoko.domain.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lokoko.domain.video.entity.ReviewVideo;

public interface ReviewVideoRepository extends JpaRepository<ReviewVideo, Long> {
}
