package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;

        // Read dynamic timeout from property, default to 30
        int timeout = Integer.parseInt(System.getProperty("wait.timeout", "30"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        System.out.println("⏱️ WebDriverWait initialized with timeout: " + timeout + " seconds");
    }

    public static String runtimePicklistNo;

    
}
