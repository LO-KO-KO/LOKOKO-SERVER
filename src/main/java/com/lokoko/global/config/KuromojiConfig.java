package com.lokoko.global.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer.Mode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KuromojiConfig {

    @Bean
    public Analyzer japaneseAnalyzer() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, Mode.SEARCH);
                return new TokenStreamComponents(tokenizer);
            }
        };
    }
}