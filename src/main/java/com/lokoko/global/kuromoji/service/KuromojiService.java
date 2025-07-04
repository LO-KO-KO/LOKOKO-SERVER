package com.lokoko.global.kuromoji.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KuromojiService {

    private final Analyzer analyzer;

    public List<String> tokenize(String text) {
        List<String> out = new ArrayList<>();
        try (TokenStream ts = analyzer.tokenStream(null, new StringReader(text))) {
            ts.reset();
            CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
            while (ts.incrementToken()) {
                out.add(termAtt.toString());
            }
            ts.end();
        } catch (IOException e) {
            throw new RuntimeException("Tokenizing failed", e);
        }
        return out;
    }

}
