package com.lokoko.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MediaFile {

    private String fileName;

    @Column(length = 1000)
    private String fileUrl;

    public MediaFile(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public static MediaFile of(String fileName, String fileUrl) {
        return new MediaFile(fileName, fileUrl);
    }

}
