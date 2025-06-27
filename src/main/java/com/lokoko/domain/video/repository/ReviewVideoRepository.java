package com.lokoko.domain.video.repository;

import com.lokoko.domain.video.entity.ReviewVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewVideoRepository extends JpaRepository<ReviewVideo, Long> {
}
