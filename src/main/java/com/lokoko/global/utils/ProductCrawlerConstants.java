package com.lokoko.global.utils;

public final class ProductCrawlerConstants {
    // Base URL & Path
    public static final String BASE_URL = "https://global.oliveyoung.com";
    public static final String PATH_DISPLAY_CATEGORY = "/display/category?ctgrNo=%s";
    // Selectors
    public static final String SELECTOR_WRAP_LNB_FILTER = ".wrap-lnb-filter";
    public static final String[] SELECTORS_SUBCATEGORY_AREA = {
            "ul.depth-3",
            "li[data-category_level='4']",
            ".category[data-category_level='4']"
    };
    public static final String SELECTOR_INPUT_SUBCATEGORY_FORMAT = "input[data-item_no='%s']";
    public static final String SELECTOR_LABEL_SUBCATEGORY_FORMAT = "label[for='%s']";
    public static final String SELECTOR_FILTER_TAG_FORMAT = "div.filter-tag span.tag[data-item_no='%s']";
    public static final String SELECTOR_CATEGORY_PRODUCT_LIST_LINK = "#categoryProductList li div.unit-thumb a";
    public static final String SELECTOR_CATEGORY_PRODUCT_LIST_ITEM = "#categoryProductList li";
    public static final String SELECTOR_TAG_BADGE = ".prd-bedge";
    public static final String TAG_TEXT_BEST = "베스트";
    public static final String TAG_TEXT_NEW = "신상품";
    public static final String SELECTOR_ZOOM_IMAGE = ".prd-visual-content .prd-unit-img img#zoom_01";
    public static final String SELECTOR_ANY_IMAGE = ".prd-visual-content .prd-unit-img img";
    public static final String SELECTOR_IMAGE_SLIDE = ".prd-visual-content .swiper-slide";
    public static final String SELECTOR_PRICE_INFO = ".prd-price-info";
    public static final String SELECTOR_PRICE_NORMAL = ".prd-price-info .price span";
    public static final String SELECTOR_PRICE_SALE = ".prd-price-info dd.sale-price";
    public static final String REGEX_YEN = "¥[0-9,]+";
    // Error messages
    public static final String ERROR_MSG_EXTRACT_ID = "Error extracting product ID from URL: ";
    public static final String ERROR_MSG_PARSE_PRICE = "Failed to parse price: ";
    public static final String ERROR_MSG_SLEEP_INTERRUPTED = "Sleep interrupted: ";

    // Selectors
    public static final String[] SELECTORS_PRODUCT_DETAIL = {
            "a[href='#agreeList1']",
            "a[data-target='#agreeList1']",
            "a:contains('商品特徴')",
            "a:contains('상세')",
            "a:contains('특징')"
    };

    public static final String[] SELECTORS_INGREDIENTS = {
            "a[href='#agreeList2']",
            "a[data-target='#agreeList2']",
            "a:contains('原材料')",
            "a:contains('성분')",
            "a:contains('원료')"
    };

    private ProductCrawlerConstants() {
    }
}
