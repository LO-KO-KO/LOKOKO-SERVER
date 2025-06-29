package com.lokoko.global.config;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YoutubeConfig {

    @Value("${youtube.api-key}")
    private String apiKey;

    @Bean
    public YouTube youtubeClient() {
        return new YouTube.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                request -> {
                }
        ).setApplicationName("lokoko-crawler")
                .build();
    }

    @Bean
    public String youtubeApiKey() {
        return apiKey;
    }
}
