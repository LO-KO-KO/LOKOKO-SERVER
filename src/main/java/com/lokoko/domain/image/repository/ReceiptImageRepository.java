package com.lokoko.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lokoko.domain.image.entity.ReceiptImage;

public interface ReceiptImageRepository extends JpaRepository<ReceiptImage, Long> {
}
