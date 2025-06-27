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
            int savedCount = (int) productRepository
                    .countByMainCategoryAndMiddleCategoryAndSubCategory(main, middle, sub);

            for (String detailUrl : detailUrls) {
                if (savedCount >= MAX_PER_SUB) {
                    break;
                }

                boolean success = scrapeDetailPageSafelyInNewTab(detailUrl, main, middle, sub);
                if (success) {
                    savedCount++;
                }
                util.safeSleep(500);
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

            String href = a.getAttribute("href");
            String abs = util.convertToAbsoluteUrl(href);
            String id = util.extractProductId(abs);

            if (id != null && seen.add(id)) {
                urls.add(abs);
            }
        }

        return urls;
    }

    private boolean scrapeDetailPageSafelyInNewTab(String url, MainCategory main, MiddleCategory middle,
                                                   SubCategory sub) {
        String originalTab = driver.getWindowHandle();
        try {
            ((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(tabs.size() - 1));
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(ProductCrawlerConstants.SELECTOR_PRICE_INFO)));
            Thread.sleep(1000);

            long price = util.extractPrice();
            String ship = util.safeText(By.cssSelector(".prd-delivery-info"));
            String brand = util.safeText(By.cssSelector(".prd-brand-info h3"));
            String detail = util.safeText(By.cssSelector(".prd-brand-info dl"));
            Tag tag = util.extractTag();

            String productDetail = null;
            String ingredients = null;

            String[] detailSelectors = {
                    "a[href='#agreeList1']",
                    "a[data-target='#agreeList1']",
                    "a:contains('商品特徴')",
                    "a:contains('상세')",
                    "a:contains('특징')"
            };

            String[] ingredientSelectors = {
                    "a[href='#agreeList2']",
                    "a[data-target='#agreeList2']",
                    "a:contains('原材料')",
                    "a:contains('성분')",
                    "a:contains('원료')"
            };

            for (String selector : detailSelectors) {
                productDetail = extractDetailOrIngredients(selector, "#agreeList1", wait, js);
                if (productDetail != null) {
                    break;
                }
            }

            for (String selector : ingredientSelectors) {
                ingredients = extractDetailOrIngredients(selector, "#agreeList2", wait, js);
                if (ingredients != null) {
                    break;
                }
            }

            if (productDetail == null && ingredients == null) {
                System.out.println("SKIP: No detail/ingredients for " + url);
                driver.close();
                driver.switchTo().window(originalTab);
                return false;
            }

            if (brand == null || detail == null) {
                driver.close();
                driver.switchTo().window(originalTab);
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
            String imgUrl = util.extractImageUrl();

            if (imgUrl != null) {
                productImageRepository.save(
                        ProductImage.builder()
                                .product(saved)
                                .url(imgUrl)
                                .build());
            }
            System.out.println("Product saved with detail: " + (productDetail != null ? "YES" : "NO") +
                    ", ingredients: " + (ingredients != null ? "YES" : "NO"));

            driver.close();
            driver.switchTo().window(originalTab);
            return true;
        } catch (Exception e) {
            System.err.println("Error in detail page scraping: " + e.getMessage());
            try {
                driver.close();
            } catch (Exception ignore) {
            }
            driver.switchTo().window(originalTab);
            return false;
        }
    }

    private String extractDetailOrIngredients(String anchorSelector, String contentId, WebDriverWait wait,
                                              JavascriptExecutor js) {
        try {
            List<WebElement> anchors = driver.findElements(By.cssSelector(anchorSelector));
            if (anchors.isEmpty()) {
                System.out.println("Anchor not found: " + anchorSelector);
                return null;
            }
            WebElement anchor = anchors.get(0);
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", anchor);
            Thread.sleep(500);
            String expanded = anchor.getAttribute("aria-expanded");
            System.out.println("Initial aria-expanded: " + expanded + " for " + anchorSelector);

            if (!"true".equals(expanded)) {
                js.executeScript("arguments[0].click();", anchor);
                boolean expandSuccess = wait.until(d -> "true".equals(anchor.getAttribute("aria-expanded")));
                System.out.println("Expand success: " + expandSuccess);
            }
            List<WebElement> contentDivs = new ArrayList<>();
            contentDivs.addAll(driver.findElements(By.cssSelector(contentId + " div")));

            if (contentDivs.isEmpty()) {
                contentDivs.addAll(driver.findElements(By.cssSelector(contentId + ".collapse.show div")));
            }

            if (contentDivs.isEmpty()) {
                contentDivs.addAll(driver.findElements(By.cssSelector(contentId + ".collapse.in div")));
            }

            if (contentDivs.isEmpty()) {
                contentDivs.addAll(driver.findElements(By.cssSelector(contentId + " > div")));
            }

            if (!contentDivs.isEmpty()) {
                String content = contentDivs.get(0).getAttribute("innerHTML");
                System.out.println(
                        "Content found: " + (content != null ? content.substring(0, Math.min(50, content.length()))
                                + "..." : "null"));
                return content;
            }
            System.out.println("No content div found for " + contentId);
            return null;
        } catch (Exception e) {
            System.err.println("Error extracting content for " + anchorSelector + ": " + e.getMessage());
            return null;
        }
    }
}
