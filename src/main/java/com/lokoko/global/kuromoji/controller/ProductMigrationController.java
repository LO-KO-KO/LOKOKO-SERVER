package com.lokoko.global.kuromoji.controller;

import com.lokoko.global.kuromoji.service.ProductMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/migration")
public class ProductMigrationController {

    private final ProductMigrationService productMigrationService;

    @PostMapping("/update-search-fields")
    public ResponseEntity<String> updateSearchFields() {
        productMigrationService.migrateSearchFields();
        return ResponseEntity.ok("검색 필드 업데이트 완료");
    }
}

