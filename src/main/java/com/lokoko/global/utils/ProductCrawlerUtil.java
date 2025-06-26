package com.lokoko.global.utils;


import com.lokoko.domain.product.entity.enums.Tag;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class ProductCrawlerUtil {

    private final WebDriver driver;

    public ProductCrawlerUtil(WebDriver driver) {
        this.driver = driver;
    }

    public String extractProductId(String url) {
        try {
            Pattern pattern = Pattern.compile("prdtNo=([^&]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println(ProductCrawlerConstants.ERROR_MSG_EXTRACT_ID + url);
        }
        return null;
    }

    public String convertToAbsoluteUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        url = url.trim();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (url.startsWith("/")) {
            return ProductCrawlerConstants.BASE_URL + url;
        }
        return ProductCrawlerConstants.BASE_URL + "/" + url;
    }

    public Tag extractTag() {
        List<WebElement> badges = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_TAG_BADGE)
        );
        if (!badges.isEmpty()) {
            String txt = badges.get(0).getText().trim();
            if (ProductCrawlerConstants.TAG_TEXT_BEST.equals(txt)) {
                return Tag.BEST;
            }
            if (ProductCrawlerConstants.TAG_TEXT_NEW.equals(txt)) {
                return Tag.NEW;
            }
        }
        return null;
    }

    public String extractImageUrl() {
        List<WebElement> zoom = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_ZOOM_IMAGE)
        );
        if (!zoom.isEmpty()) {
            return zoom.get(0).getAttribute("src");
        }
        List<WebElement> imgs = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_ANY_IMAGE)
        );
        if (!imgs.isEmpty()) {
            return imgs.get(0).getAttribute("src");
        }
        WebElement slide = driver.findElement(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_IMAGE_SLIDE)
        );
        String bg = slide.getCssValue("background-image");
        if (bg != null && bg.startsWith("url")) {
            return bg.replaceFirst("^url\\([\"']?", "")
                    .replaceFirst("[\"']?\\)$", "");
        }
        return null;
    }

    public long extractPrice() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_PRICE_INFO)
        ));

        List<WebElement> normal = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_PRICE_NORMAL)
        );
        if (!normal.isEmpty()) {
            return parseYenOnly(normal.get(0).getText());
        }

        List<WebElement> sale = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_PRICE_SALE)
        );
        if (!sale.isEmpty()) {
            Matcher m = Pattern.compile(ProductCrawlerConstants.REGEX_YEN)
                    .matcher(sale.get(0).getText());
            if (m.find()) {
                return parseYenOnly(m.group());
            }
        }
        return 0;
    }

    public long parseYenOnly(String text) {
        String s = text.replace("¥", "").replace(",", "").trim();
        int idx = s.indexOf("(");
        if (idx != -1) {
            s = s.substring(0, idx).trim();
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            System.err.println(ProductCrawlerConstants.ERROR_MSG_PARSE_PRICE + s);
            return 0;
        }
    }

    public String safeText(By sel) {
        try {
            String t = driver.findElement(sel).getText();
            return (t == null || t.isBlank()) ? null : t.trim();
        } catch (Exception e) {
            return null;
        }
    }

    public void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(ProductCrawlerConstants.ERROR_MSG_SLEEP_INTERRUPTED + e.getMessage());
        }
    }
}
