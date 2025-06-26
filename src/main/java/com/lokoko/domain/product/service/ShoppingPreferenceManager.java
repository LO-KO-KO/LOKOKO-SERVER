package com.lokoko.domain.product.service;

import java.time.Duration;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class ShoppingPreferenceManager {

    private static final String BASE_URL = "https://global.oliveyoung.com";
    private final WebDriver driver;

    public ShoppingPreferenceManager(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * 최초 진입 시 “일본” 쇼핑 환경으로 변경해 주는 메서드
     */
    public void ensureJapanCountry() {
        driver.get(BASE_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1) 혹시 떠 있는 네이티브 alert 닫기
        try {
            Alert alert = wait.withTimeout(Duration.ofSeconds(3))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (Exception ignored) {
        }

        // 2) “You appear to be in KOREA…” 팝업이 떠 있으면 닫기
        try {
            WebElement sysPopup = driver.findElement(By.id("deliveryCountrySelectPopup"));
            if (sysPopup.isDisplayed()) {
                WebElement chooseKor = sysPopup.findElement(By.id("change-cntry-btn-kor"));
                js.executeScript("arguments[0].click();", chooseKor);
                wait.until(ExpectedConditions.invisibilityOf(sysPopup));
            }
        } catch (Exception ignored) {
        }

        // 3) 오른쪽 상단 언어/국가 선택 레이어 열기
        WebElement langToggle = wait.until(ExpectedConditions
                .presenceOfElementLocated(By.cssSelector("a.btn-lang.popup-layer-trigger")));
        js.executeScript("arguments[0].click();", langToggle);

        // 4) country-select 레이어 로딩 대기
        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("deliveryCountrySelectbox")));

        // 5) 드롭다운 토글 열기
        WebElement countryToggle = wait.until(ExpectedConditions
                .elementToBeClickable(By.id("sel_headDlvCntry")));
        js.executeScript("arguments[0].click();", countryToggle);

        // 6) aria-expanded 가 true 로 바뀔 때까지 대기
        wait.until(drv -> "true".equals(
                drv.findElement(By.id("deliveryCountrySelectbox"))
                        .getAttribute("aria-expanded")
        ));

        // 7) JAPAN 선택
        WebElement japan = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector(
                        "ul.selectbox-options#headDlvCntry li[data-code-dtl-no='110']")));
        js.executeScript("arguments[0].click();", japan);

        // 8) JPY 통화 선택
        WebElement jpyCurrency = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector(
                        "span[data-code-dtl-no='JPY'][data-option='currencyCodeList']")));
        js.executeScript("arguments[0].click();", jpyCurrency);
        
        // 8) Save 버튼 클릭
        WebElement saveBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.id("curSave")));
        js.executeScript("arguments[0].click();", saveBtn);

        // 9a) 혹시 뜨는 네이티브 alert 닫기
        try {
            Alert alert2 = wait.withTimeout(Duration.ofSeconds(3))
                    .until(ExpectedConditions.alertIsPresent());
            alert2.accept();
        } catch (Exception ignored) {
        }

        // 9b) HTML 팝업(“配送国/地域を変更…” 메시지) 의 “확인” 클릭
        try {
            WebElement confirm = wait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath(
                            "//div[contains(@class,'popup-layer-body')]//button[normalize-space()='확인']"
                    )));
            js.executeScript("arguments[0].click();", confirm);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.popup-layer-body")));
        } catch (Exception ignored) {
        }

        // 10) country-select 레이어가 완전히 닫힐 때까지 대기
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("deliveryCountrySelectPopup")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("deliveryCountrySelectbox")));
    }
}
