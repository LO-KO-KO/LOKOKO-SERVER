package com.lokoko.domain.product.service;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.global.utils.ProductCrawlerConstants;
import com.lokoko.global.utils.ProductCrawlerUtil;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {
    private static final int MAX_PER_SUB = 5;
    private static final int SAFETY_SLEEP = 300;

    private final WebDriver driver;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ShoppingPreferenceManager preferenceManager;
    private final ProductCrawlerUtil util;

    @Transactional
    public void scrapeByCategory(MainCategory main, MiddleCategory middle) {
        preferenceManager.ensureJapanCountry();
        List<SubCategory> subs = List.of(SubCategory.values());
        subs.stream()
                .filter(sc -> sc.getMiddleCategory() == middle)
                .forEach(sub -> scrapeBySub(main, middle, sub));
    }

    private void scrapeBySub(MainCategory main, MiddleCategory middle, SubCategory sub) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ProductCrawlerConstants.DEFAULT_WAIT_SEC));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            String url = String.format(ProductCrawlerConstants.BASE_URL +
                    ProductCrawlerConstants.PATH_DISPLAY_CATEGORY, middle.getCtgrNo());
            driver.get(url);
            log.info("크롤링 시작 URL 접근: {}", url);

            util.waitForPresence(ProductCrawlerConstants.SELECTOR_WRAP_LNB_FILTER, 15);
            util.waitForPresence(ProductCrawlerConstants.SELECTORS_SUBCATEGORY_AREA[0], 15);
            util.waitForNonEmpty(ProductCrawlerConstants.SELECTORS_SUBCATEGORY_AREA[1], 15);

            clearOtherSubCategories(middle, sub, js);
            selectTargetSubCategory(sub, js);

            util.waitForPresence(
                    String.format(ProductCrawlerConstants.SELECTOR_FILTER_TAG_FORMAT, sub.getCtgrNo()),
                    15);
            util.waitForNonEmpty(ProductCrawlerConstants.SELECTOR_CATEGORY_PRODUCT_LIST_ITEM, 15);
            util.safeSleep(ProductCrawlerConstants.DEFAULT_SLEEP_AFTER_FILTER_MS);

            List<String> detailUrls = util.collectProductUrls(
                    By.cssSelector(ProductCrawlerConstants.SELECTOR_CATEGORY_PRODUCT_LIST_LINK)
            );
            int savedCount = (int) productRepository
                    .countByMainCategoryAndMiddleCategoryAndSubCategory(main, middle, sub);

            for (String detailUrl : detailUrls) {
                if (savedCount >= MAX_PER_SUB) {
                    break;
                }
                if (scrapeDetailPageSafelyInNewTab(detailUrl)) {
                    savedCount++;
                }
                util.safeSleep(ProductCrawlerConstants.SHORT_WAIT_SEC);
            }

        } catch (Exception e) {
            log.error("크롤링 실패: {} -> {} -> {}", main, middle, sub, e);
        }
    }

    private void clearOtherSubCategories(MiddleCategory middle, SubCategory target,
                                         JavascriptExecutor js) {
        List<SubCategory> all = List.of(SubCategory.values());
        all.stream()
                .filter(sc -> sc.getMiddleCategory() == middle && !sc.equals(target))
                .forEach(other -> {
                    String selector = String.format(
                            ProductCrawlerConstants.SELECTOR_INPUT_SUBCATEGORY_FORMAT,
                            other.getCtgrNo());
                    driver.findElements(By.cssSelector(selector)).stream()
                            .filter(WebElement::isSelected)
                            .findFirst()
                            .ifPresent(cb -> {
                                util.scrollAndClick(cb);
                                util.safeSleep(SAFETY_SLEEP);
                            });
                });
    }

    private void selectTargetSubCategory(SubCategory sub, JavascriptExecutor js) {
        String labelSelector = String.format(
                ProductCrawlerConstants.SELECTOR_LABEL_SUBCATEGORY_FORMAT,
                sub.getCtgrNo());
        try {
            WebElement lbl = driver.findElement(By.cssSelector(labelSelector));
            util.scrollAndClick(lbl);
        } catch (Exception e) {
            String inputSelector = String.format(
                    ProductCrawlerConstants.SELECTOR_INPUT_SUBCATEGORY_FORMAT,
                    sub.getCtgrNo());
            WebElement inp = driver.findElement(By.cssSelector(inputSelector));
            util.scrollAndClick(inp);
        }
    }

    private boolean scrapeDetailPageSafelyInNewTab(String url) {
        String originalTab = util.openNewTabAndSwitch();
        try {
            driver.get(url);
            util.waitForPresence(ProductCrawlerConstants.SELECTOR_PRICE_INFO, 15);
            util.safeSleep(1000);

            long price = util.extractPrice();
            String ship = util.safeText(By.cssSelector(".prd-delivery-info"));
            String brand = util.safeText(By.cssSelector(".prd-brand-info h3"));
            String detail = util.safeText(By.cssSelector(".prd-brand-info dl"));
            Tag tag = util.extractTag();

            String productDetail = util.expandAndExtract(
                    ProductCrawlerConstants.SELECTORS_PRODUCT_DETAIL,
                    "#agreeList1"
            );
            String ingredients = util.expandAndExtract(
                    ProductCrawlerConstants.SELECTORS_INGREDIENTS,
                    "#agreeList2"
            );

            if ((productDetail == null && ingredients == null)
                    || brand == null || detail == null) {
                return false;
            }

            Product p = Product.builder()
                    .normalPrice(price)
                    .brandName(brand)
                    .productName(detail)
                    .shippingInfo(ship != null ? ship : "배송 정보 없음")
                    .tag(tag)
                    .mainCategory(null) // 필요시 매개변수로 전달
                    .middleCategory(null)
                    .subCategory(null)
                    .productDetail(productDetail)
                    .ingredients(ingredients)
                    .oliveYoungUrl(url)
                    .build();

            Product saved = productRepository.save(p);
            util.extractImageUrl().ifPresent(imgUrl ->
                    productImageRepository.save(
                            ProductImage.builder()
                                    .product(saved)
                                    .url(imgUrl)
                                    .build()
                    )
            );

            return true;
        } catch (Exception e) {
            log.error("상세 페이지 크롤링 오류 (URL={}): {}", url, e.getMessage(), e);
            return false;
        } finally {
            util.closeCurrentTabAndSwitchBack(originalTab);
        }
    }
}

