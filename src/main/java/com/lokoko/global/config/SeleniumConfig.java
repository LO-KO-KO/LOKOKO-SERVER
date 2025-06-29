package com.lokoko.global.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @Bean
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--user-data-dir=/tmp/chrome-data"
        );

        return new ChromeDriver(options);
    }
}
