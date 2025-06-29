package com.lokoko.global.utils;


import com.lokoko.domain.product.entity.enums.Tag;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCrawlerUtil {
    private final WebDriver driver;

    public void waitForPresence(String cssSelector, long timeoutSec) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
    }

    public void waitForNonEmpty(String cssSelector, long timeoutSec) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(d -> !d.findElements(By.cssSelector(cssSelector)).isEmpty());
    }

    public void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        safeSleep(ProductCrawlerConstants.SAFETY_SLEEP_MS);
        element.click();
    }

    public String openNewTabAndSwitch() {
        ((JavascriptExecutor) driver)
                .executeScript("window.open('about:blank','_blank');");
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        String newTab = tabs.get(tabs.size() - 1);
        driver.switchTo().window(newTab);
        return tabs.get(0); // original tab
    }

    public void closeCurrentTabAndSwitchBack(String originalTab) {
        driver.close();
        driver.switchTo().window(originalTab);
    }

    public List<String> collectProductUrls(By linkSelector) {
        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        driver.findElements(linkSelector).forEach(a -> {
            String href = a.getAttribute("href");
            String abs = convertToAbsoluteUrl(href);
            String id = extractProductId(abs);
            if (id != null && seen.add(id)) {
                urls.add(abs);
            }
        });
        return urls;
    }

    public String expandAndExtract(String[] anchorSelectors, String contentCss) {
        for (String sel : anchorSelectors) {
            try {
                List<WebElement> anchors = driver.findElements(By.cssSelector(sel));
                if (anchors.isEmpty()) {
                    continue;
                }
                WebElement anchor = anchors.get(0);
                scrollAndClick(anchor);
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(d -> "true".equals(anchor.getAttribute("aria-expanded")));

                List<WebElement> divs = driver.findElements(By.cssSelector(contentCss + " div"));
                if (divs.isEmpty()) {
                    continue;
                }
                return divs.get(0).getAttribute("innerHTML");
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public String extractProductId(String url) {
        try {
            Pattern pattern = Pattern.compile("prdtNo=([^&]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            log.error("{}{}", ProductCrawlerConstants.ERROR_MSG_EXTRACT_ID, url, e);
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

    public Optional<String> extractImageUrl() {
        List<WebElement> zoom = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_ZOOM_IMAGE)
        );
        if (!zoom.isEmpty()) {
            return Optional.ofNullable(zoom.get(0).getAttribute("src"));
        }

        List<WebElement> imgs = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_ANY_IMAGE)
        );
        if (!imgs.isEmpty()) {
            return Optional.ofNullable(imgs.get(0).getAttribute("src"));
        }

        List<WebElement> slides = driver.findElements(
                By.cssSelector(ProductCrawlerConstants.SELECTOR_IMAGE_SLIDE)
        );
        if (!slides.isEmpty()) {
            String bg = slides.get(0).getCssValue("background-image");
            if (bg != null && bg.startsWith("url")) {
                String url = bg.replaceFirst("^url\\([\"']?", "")
                        .replaceFirst("[\"']?\\)$", "");
                return Optional.ofNullable(url);
            }
        }

        return Optional.empty();
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
            log.error("{}{}", ProductCrawlerConstants.ERROR_MSG_PARSE_PRICE, s, e);
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
            log.error("{}{}", ProductCrawlerConstants.ERROR_MSG_SLEEP_INTERRUPTED, e.getMessage(), e);
        }
    }
}
