package com.lokoko.global.utils;

public final class ProductCrawlerConstants {
    // Base URL & Path
    public static final String BASE_URL = "https://global.oliveyoung.com";
    public static final String PATH_DISPLAY_CATEGORY = "/display/category?ctgrNo=%s";

    // 필터
    public static final String SELECTOR_WRAP_LNB_FILTER = ".wrap-lnb-filter";
    public static final String[] SELECTORS_SUBCATEGORY_AREA = {
            "ul.depth-3",
            "li[data-category_level='4']",
            ".category[data-category_level='4']"
    };
    public static final String SELECTOR_INPUT_SUBCATEGORY_FORMAT = "input[data-item_no='%s']";
    public static final String SELECTOR_LABEL_SUBCATEGORY_FORMAT = "label[for='%s']";
    public static final String SELECTOR_FILTER_TAG_FORMAT = "div.filter-tag span.tag[data-item_no='%s']";

    // 상품 리스트 (최신·신제품 공통)
    public static final String SELECTOR_CATEGORY_PRODUCT_LIST_LINK = "#categoryProductList li div.unit-thumb a";
    public static final String SELECTOR_CATEGORY_PRODUCT_LIST_ITEM = "#categoryProductList li";

    // 태그 뱃지
    public static final String SELECTOR_TAG_BADGE = ".prd-bedge";
    public static final String TAG_TEXT_BEST = "베스트";
    public static final String TAG_TEXT_NEW = "신상품";

    // 이미지
    public static final String SELECTOR_ZOOM_IMAGE = ".prd-visual-content .prd-unit-img img#zoom_01";
    public static final String SELECTOR_ANY_IMAGE = ".prd-visual-content .prd-unit-img img";
    public static final String SELECTOR_IMAGE_SLIDE = ".prd-visual-content .swiper-slide";

    // 상세페이지
    public static final String SELECTOR_PRICE_INFO = ".prd-price-info";
    public static final String SELECTOR_PRICE_NORMAL = ".prd-price-info .price span";
    public static final String SELECTOR_PRICE_SALE = ".prd-price-info dd.sale-price";
    public static final String REGEX_YEN = "¥[0-9,]+";
    public static final String[] SELECTORS_PRODUCT_DETAIL = {
            "a[href='#agreeList1']", "a[data-target='#agreeList1']",
            "a:contains('商品特徴')", "a:contains('상세')", "a:contains('특징')"
    };
    public static final String[] SELECTORS_INGREDIENTS = {
            "a[href='#agreeList2']", "a[data-target='#agreeList2']",
            "a:contains('原材料')", "a:contains('성분')", "a:contains('원료')"
    };

    // 공통 대기·슬립
    public static final long SAFETY_SLEEP_MS = 300L;
    public static final long DEFAULT_SLEEP_AFTER_FILTER_MS = 2000L;
    public static final long DEFAULT_WAIT_SEC = 15L;
    public static final long SHORT_WAIT_SEC = 5L;

    // 에러 메시지
    public static final String ERROR_MSG_EXTRACT_ID = "Error extracting product ID from URL: ";
    public static final String ERROR_MSG_PARSE_PRICE = "Failed to parse price: ";
    public static final String ERROR_MSG_SLEEP_INTERRUPTED = "Sleep interrupted: ";

    private ProductCrawlerConstants() {
    }
}
