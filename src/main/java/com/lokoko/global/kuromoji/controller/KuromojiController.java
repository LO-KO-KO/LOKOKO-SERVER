package com.lokoko.global.kuromoji.controller;

import com.lokoko.global.kuromoji.service.KuromojiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kuromoji")
@RequiredArgsConstructor
public class KuromojiController {

    private final KuromojiService kuromojiService;

    @GetMapping("/tokenize")
    public List<String> tokenize(@RequestParam String text) {
        return kuromojiService.tokenize(text);
    }
}