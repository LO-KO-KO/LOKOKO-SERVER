package com.lokoko.domain.product.service;

import static com.lokoko.global.utils.ProductCrawlerConstants.SELECTOR_OPTION_BUTTON;
import static com.lokoko.global.utils.ProductCrawlerConstants.SELECTOR_OPTION_LIST;
import static com.lokoko.global.utils.ProductCrawlerConstants.SELECTOR_OPTION_NAME;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.ProductOption;
import com.lokoko.domain.product.entity.enums.MainCategory;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.product.entity.enums.Tag;
import com.lokoko.domain.product.repository.ProductOptionRepository;
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
public class ProductCrawlingService {
    private static final int MAX_PER_SUB = 5;
    private final WebDriver driver;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
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

    @Transactional
    public void crawlAllOptions() {
        List<Product> products = productRepository.findAllByOliveYoungUrlNotNull();

        for (Product p : products) {
            if (!productOptionRepository.findByProduct(p).isEmpty()) {
                continue;
            }
            String originalTab = util.openNewTabAndSwitch();
            try {
                driver.get(p.getOliveYoungUrl());
                util.waitForPresence(ProductCrawlerConstants.SELECTOR_PRICE_INFO, 10);

                List<WebElement> optionBtns = driver.findElements(
                        By.cssSelector(SELECTOR_OPTION_BUTTON));
                if (optionBtns.isEmpty()) {
                    continue;
                }

                By btn = By.cssSelector(SELECTOR_OPTION_BUTTON);
                WebElement optionButton = util.waitForElementVisible(btn, 5);
                if ("false".equals(optionButton.getAttribute("aria-expanded"))) {
                    optionButton.click();
                    util.safeSleep(500);
                }
                By listItems = By.cssSelector(SELECTOR_OPTION_LIST);
                util.waitForPresence(listItems, 5);
                List<WebElement> names = driver.findElements(
                        By.cssSelector(SELECTOR_OPTION_NAME)
                );
                for (WebElement nameEl : names) {
                    String optionName = nameEl.getText().trim();
                    if (!optionName.isEmpty()) {
                        ProductOption opt = ProductOption.builder()
                                .product(p)
                                .optionName(optionName)
                                .build();
                        productOptionRepository.save(opt);
                    }
                }
            } catch (Exception ex) {
                log.error("옵션 크롤링 실패 URL={} : {}", p.getOliveYoungUrl(), ex.getMessage());
            } finally {
                util.closeCurrentTabAndSwitchBack(originalTab);
            }
        }
    }

    private void scrapeBySub(MainCategory main, MiddleCategory middle, SubCategory sub) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ProductCrawlerConstants.DEFAULT_WAIT_SEC));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            String url = String.format(ProductCrawlerConstants.BASE_URL +
                    ProductCrawlerConstants.PATH_DISPLAY_CATEGORY, middle.getCtgrNo());
            driver.get(url);
            util.waitForPresence(ProductCrawlerConstants.SELECTOR_WRAP_LNB_FILTER, 15);
            By subInputLocator = By.cssSelector(
                    String.format(ProductCrawlerConstants.SELECTOR_INPUT_SUBCATEGORY_FORMAT, sub.getCtgrNo())
            );
            util.waitForPresence(subInputLocator, 15);

            By subLabelLocator = By.cssSelector(
                    String.format(ProductCrawlerConstants.SELECTOR_LABEL_SUBCATEGORY_FORMAT, sub.getCtgrNo())
            );
            util.waitForPresence(subLabelLocator, 5);

            util.clearOtherSubCategories(middle, sub, js);
            selectTargetSubCategory(sub, js);
            util.waitForNonEmpty(ProductCrawlerConstants.SELECTOR_CATEGORY_PRODUCT_LIST_ITEM, 15);
            util.safeSleep(ProductCrawlerConstants.DEFAULT_SLEEP_AFTER_FILTER_MS);
            List<String> detailUrls = util.collectUniqueProductUrls(sub);
            int savedCount = (int) productRepository
                    .countByMainCategoryAndMiddleCategoryAndSubCategory(main, middle, sub);

            for (String detailUrl : detailUrls) {
                if (savedCount >= MAX_PER_SUB) {
                    break;
                }
                if (scrapeDetailPageSafelyInNewTab(detailUrl, main, middle, sub)) {
                    savedCount++;
                }
                util.safeSleep(ProductCrawlerConstants.SHORT_WAIT_SEC);
            }

        } catch (Exception e) {
            log.error("크롤링 실패: {} -> {} -> {}", main, middle, sub, e);
        }
    }

    private boolean scrapeDetailPageSafelyInNewTab(String url, MainCategory main, MiddleCategory middle,
                                                   SubCategory sub) {
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

            By optionBtnLocator = By.cssSelector(SELECTOR_OPTION_BUTTON);
            WebElement optionButton = util.waitForElementVisible(optionBtnLocator, 5);
            if ("false".equals(optionButton.getAttribute("aria-expanded"))) {
                optionButton.click();
            }

            By listLocator = By.cssSelector(SELECTOR_OPTION_LIST);
            util.waitForPresence(listLocator, 5);

            List<WebElement> optionItems = driver.findElements(
                    By.cssSelector(SELECTOR_OPTION_NAME)
            );
            for (WebElement item : optionItems) {
                String optionName = item.getText().trim();
                if (!optionName.isEmpty()) {
                    ProductOption opt = ProductOption.builder()
                            .optionName(optionName)
                            .product(saved)
                            .build();
                    productOptionRepository.save(opt);
                }
            }

            return true;
        } catch (Exception e) {
            log.error("상세 페이지 크롤링 오류 (URL={}): {}", url, e.getMessage(), e);
            return false;
        } finally {
            util.closeCurrentTabAndSwitchBack(originalTab);
        }
    }

    public void selectTargetSubCategory(SubCategory sub, JavascriptExecutor js) {
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
}
