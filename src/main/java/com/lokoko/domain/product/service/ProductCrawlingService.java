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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCrawlingService {
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
        List<SubCategory> subs = Arrays.stream(SubCategory.values())
                .filter(sc -> sc.getMiddleCategory() == middle)
                .toList();

        for (SubCategory sub : subs) {
            scrapeBySub(main, middle, sub);
        }
    }

    private void scrapeBySub(MainCategory main, MiddleCategory middle, SubCategory sub) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            String url = ProductCrawlerConstants.BASE_URL +
                    String.format(ProductCrawlerConstants.PATH_DISPLAY_CATEGORY, middle.getCtgrNo());
            driver.get(url);
            System.out.println("Accessed: " + url);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(ProductCrawlerConstants.SELECTOR_WRAP_LNB_FILTER)));
            waitForSubcategoryArea(wait);

            clearOtherSubCategories(middle, sub, js, wait);
            selectTargetSubCategory(sub, js, wait);
            waitForFilterApplication(sub, wait);

            List<String> detailUrls = collectUniqueProductUrls(sub);
            for (String detailUrl : detailUrls) {
                long count = productRepository
                        .countByMainCategoryAndMiddleCategoryAndSubCategory(main, middle, sub);

                if (count >= MAX_PER_SUB) {
                    break;
                }
                scrapeDetailPageSafely(detailUrl, main, middle, sub);
            }

        } catch (Exception e) {
            System.err.printf("Failed to scrape %s->%s->%s: %s%n",
                    main, middle, sub, e.getMessage());
        }
    }

    private void waitForSubcategoryArea(WebDriverWait wait) {
        try {
            for (String sel : ProductCrawlerConstants.SELECTORS_SUBCATEGORY_AREA) {
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(sel)));

                    return;
                } catch (Exception ignored) {
                }
            }
            wait.until(d -> !d.findElements(
                            By.cssSelector(ProductCrawlerConstants.SELECTORS_SUBCATEGORY_AREA[1]))
                    .isEmpty());
        } catch (Exception e) {
            throw e;
        }
    }

    private void clearOtherSubCategories(MiddleCategory middle, SubCategory target,
                                         JavascriptExecutor js, WebDriverWait wait) {
        List<SubCategory> all = Arrays.stream(SubCategory.values())
                .filter(sc -> sc.getMiddleCategory() == middle).toList();
        for (SubCategory other : all) {
            if (!other.equals(target)) {
                String fmt = ProductCrawlerConstants.SELECTOR_INPUT_SUBCATEGORY_FORMAT;
                List<WebElement> cbs = driver.findElements(
                        By.cssSelector(String.format(fmt, other.getCtgrNo())));

                if (!cbs.isEmpty() && cbs.get(0).isSelected()) {
                    js.executeScript("arguments[0].click();", cbs.get(0));
                    util.safeSleep(SAFETY_SLEEP);
                }
            }
        }
    }

    private void selectTargetSubCategory(SubCategory sub, JavascriptExecutor js,
                                         WebDriverWait wait) {
        try {
            String lblFmt = ProductCrawlerConstants.SELECTOR_LABEL_SUBCATEGORY_FORMAT;
            WebElement lbl = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(String.format(lblFmt, sub.getCtgrNo()))));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", lbl);
            util.safeSleep(SAFETY_SLEEP);
            js.executeScript("arguments[0].click();", lbl);
        } catch (Exception e) {
            String inFmt = ProductCrawlerConstants.SELECTOR_INPUT_SUBCATEGORY_FORMAT;
            WebElement inp = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(String.format(inFmt, sub.getCtgrNo()))));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", inp);
            util.safeSleep(SAFETY_SLEEP);
            js.executeScript("arguments[0].click();", inp);
        }
    }

    private void waitForFilterApplication(SubCategory sub, WebDriverWait wait) {
        try {
            String fmt = ProductCrawlerConstants.SELECTOR_FILTER_TAG_FORMAT;
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(String.format(fmt, sub.getCtgrNo()))));
            wait.until(d -> !d.findElements(
                            By.cssSelector(ProductCrawlerConstants.SELECTOR_CATEGORY_PRODUCT_LIST_ITEM))
                    .isEmpty());
            util.safeSleep(2000);

        } catch (Exception e) {
        }
    }

    private List<String> collectUniqueProductUrls(SubCategory sub) {
        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (WebElement a : driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_CATEGORY_PRODUCT_LIST_LINK))) {

            if (urls.size() >= MAX_PER_SUB) {
                break;
            }

            String href = a.getAttribute("href");
            String abs = util.convertToAbsoluteUrl(href);
            String id = util.extractProductId(abs);

            if (id != null && seen.add(id)) {
                urls.add(abs);
            }
        }

        return urls;
    }

    private boolean scrapeDetailPageSafely(String url, MainCategory main, MiddleCategory middle, SubCategory sub) {
        try {
            driver.get(url);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(ProductCrawlerConstants.SELECTOR_PRICE_INFO)));
            long price = util.extractPrice();
            String ship = util.safeText(By.cssSelector(".prd-delivery-info"));
            String brand = util.safeText(By.cssSelector(".prd-brand-info h3"));
            String detail = util.safeText(By.cssSelector(".prd-brand-info dl"));
            Tag tag = util.extractTag();

            if (brand == null || detail == null) {
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
                    .build();

            Product saved = productRepository.save(p);
            String imgUrl = util.extractImageUrl();

            if (imgUrl != null) {
                productImageRepository.save(
                        ProductImage.builder()
                                .product(saved)
                                .url(imgUrl)
                                .build());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}