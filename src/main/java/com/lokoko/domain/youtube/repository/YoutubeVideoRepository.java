package com.lokoko.domain.youtube.repository;

import com.lokoko.domain.video.entity.YoutubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, Long> {
    
}
