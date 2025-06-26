package com.lokoko.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubCategory {
    // ── 스킨케어/페이셜케어
    TONER("토너", MiddleCategory.FACIAL_CARE, "1000000013"),
    MOISTURIZER("모이스처라이저", MiddleCategory.FACIAL_CARE, "1000000014"),
    ESSENCE_SERUM("에센스/세럼", MiddleCategory.FACIAL_CARE, "1000000015"),
    CREAM("크림", MiddleCategory.FACIAL_CARE, "1000000016"),

    // ── 메이크업/얼굴 메이크업
    FOUNDATION("파운데이션", MiddleCategory.FACE_MAKEUP, "1000000034"),
    POWDER_COMPACT("파우더/팩트", MiddleCategory.FACE_MAKEUP, "1000000036"),
    CONCEALER("컨실러", MiddleCategory.FACE_MAKEUP, "1000000037"),
    BLUSHER("블러셔", MiddleCategory.FACE_MAKEUP, "1000000038"),

    // ── 메이크업/아이 메이크업
    EYEBROW("아이브로우", MiddleCategory.EYE_MAKEUP, "1000000041"),
    EYESHADOW("아이섀도우", MiddleCategory.EYE_MAKEUP, "1000000042"),
    EYELINER("아이라이너", MiddleCategory.EYE_MAKEUP, "1000000043"),

    // ── 메이크업/립 메이크업
    LIPSTICK("립스틱", MiddleCategory.LIP_MAKEUP, "1000000046"),
    LIP_TINT("립틴트", MiddleCategory.LIP_MAKEUP, "1000000047");

    private final String displayName;
    private final MiddleCategory middleCategory;
    private final String ctgrNo;
}
