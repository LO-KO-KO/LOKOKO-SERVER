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
public class NewProductCrawlingService {
    private static final int MAX_PER_SUB = 5;

    private final WebDriver driver;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ShoppingPreferenceManager preferenceManager;
    private final ProductCrawlerUtil util;

    @Transactional
    public void scrapeNewByCategory(MainCategory main, MiddleCategory middle) {
        preferenceManager.ensureJapanCountry();
        List<SubCategory> subs = List.of(SubCategory.values());
        subs.stream()
                .filter(sc -> sc.getMiddleCategory() == middle)
                .forEach(sub -> scrapeNewBySub(main, middle, sub));
    }

    private void scrapeNewBySub(MainCategory main,
                                MiddleCategory middle,
                                SubCategory sub) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            String url = String.format(
                    ProductCrawlerConstants.BASE_URL +
                            ProductCrawlerConstants.PATH_DISPLAY_CATEGORY,
                    middle.getCtgrNo()
            );
            driver.get(url);

            util.waitForPresence(ProductCrawlerConstants.SELECTOR_WRAP_LNB_FILTER, 20);
            util.clearOtherSubCategories(middle, sub, js);
            util.selectTargetSubCategory(sub, js);
            util.safeSleep(ProductCrawlerConstants.DEFAULT_SLEEP_AFTER_FILTER_MS);

            clickSortOption(js, "20");
            waitForSortApplied(ProductCrawlerConstants.TAG_TEXT_NEW, 20);

            util.waitForNonEmpty(
                    ProductCrawlerConstants.SELECTOR_CATEGORY_PRODUCT_LIST_ITEM, 25
            );
            util.safeSleep(3_000);

            List<String> detailUrls = driver.findElements(
                            By.cssSelector(ProductCrawlerConstants.SELECTOR_CATEGORY_PRODUCT_LIST_LINK)
                    )
                    .stream()
                    .map(a -> a.getAttribute("href"))
                    .filter(h -> h != null && h.contains("prdtNo="))
                    .map(h -> h.startsWith("http")
                            ? h
                            : ProductCrawlerConstants.BASE_URL + h)
                    .distinct()
                    .toList();

            int saved = 0;
            for (String detailUrl : detailUrls) {
                if (saved >= MAX_PER_SUB) {
                    break;
                }
                if (scrapeNewDetailPageSafelyInNewTab(
                        detailUrl, main, middle, sub
                )) {
                    saved++;
                }
                util.safeSleep(ProductCrawlerConstants.SHORT_WAIT_SEC);
            }

        } catch (Exception e) {
            log.error("신제품 크롤링 실패: {} > {} > {}", main, middle, sub, e);
        }
    }

    private boolean scrapeNewDetailPageSafelyInNewTab(String url,
                                                      MainCategory main, MiddleCategory middle, SubCategory sub) {

        String originalTab = util.openNewTabAndSwitch();
        try {
            driver.get(url);
            util.waitForPresence(ProductCrawlerConstants.SELECTOR_PRICE_INFO, 20);
            util.safeSleep(2_000);

            long price = util.extractPrice();
            String ship = util.safeText(By.cssSelector(".prd-delivery-info"));
            String brand = util.safeText(By.cssSelector(".prd-brand-info h3"));
            String detail = util.safeText(By.cssSelector(".prd-brand-info dl"));
            Tag tag = util.extractTag();
            String productDetail = util.expandAndExtract(
                    ProductCrawlerConstants.SELECTORS_PRODUCT_DETAIL, "#agreeList1"
            );
            String ingredients = util.expandAndExtract(
                    ProductCrawlerConstants.SELECTORS_INGREDIENTS, "#agreeList2"
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
                    .mainCategory(main)
                    .middleCategory(middle)
                    .subCategory(sub)
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
            log.error("신제품 상세 페이지 크롤링 오류 (URL={}): {}", url, e.getMessage());
            return false;
        } finally {
            util.closeCurrentTabAndSwitchBack(originalTab);
        }
    }

    private void clickSortOption(JavascriptExecutor js, String sortValue) {
        try {
            WebElement sortButton = driver.findElement(By.id("prdtSortStdrCode"));
            String beforeText = sortButton.getText();
            util.scrollAndClick(sortButton);
            util.safeSleep(500);

            By optionListLocator = By.cssSelector("ul.sort-list");
            WebElement optionList = util.waitForElementVisible(optionListLocator, 5);
            List<WebElement> options = optionList.findElements(By.tagName("li"));

            for (WebElement opt : options) {
                if (sortValue.equals(opt.getAttribute("value"))) {
                    util.scrollAndClick(opt);
                    util.safeSleep(1000);

                    String afterText = driver.findElement(By.id("prdtSortStdrCode")).getText();
                    log.info("정렬 변경: {} → {}", beforeText, afterText);
                    return;
                }
            }
            log.warn("정렬 옵션 미발견: value={}", sortValue);
        } catch (Exception e) {
            log.error("정렬 처리 실패: {}", e.getMessage());
        }
    }

    private void waitForSortApplied(String expectedText, int timeoutSec) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until((WebDriver d) -> {
                    try {
                        WebElement sortBtn = d.findElement(By.id("prdtSortStdrCode"));
                        return sortBtn.getText().contains(expectedText);
                    } catch (Exception e) {
                        return false;
                    }
                });
    }
}
