package com.lokoko.domain.youtube.service;

import com.lokoko.domain.video.entity.YoutubeVideo;
import com.lokoko.domain.youtube.repository.YoutubeVideoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoSaveService {
    private final YoutubeVideoRepository repository;

    @Transactional
    public void replaceAll(List<YoutubeVideo> videos) {
        log.info("기존 영상 {}개 삭제 시작", repository.count());
        repository.deleteAll();
        log.info("삭제 완료, 새로 저장할 개수={}", videos.size());
        repository.saveAll(videos);
    }
}
