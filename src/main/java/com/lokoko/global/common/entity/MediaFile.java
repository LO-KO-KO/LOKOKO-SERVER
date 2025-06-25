package com.lokoko.global.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class MediaFile {

    private String fileName;
    private String fileUrl;

    public MediaFile(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public static MediaFile of(String fileName, String fileUrl) {
        return new MediaFile(fileName, fileUrl);
    }

}
