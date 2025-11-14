
package com.example.tests.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestUtils {

    public static void ensurePageReady(WebDriver driver, WebDriverWait wait) {
        try {
            wait.until((ExpectedCondition<Boolean>) wd ->
                    ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
            Thread.sleep(500);
        } catch (Exception ignored) {}
    }

    public static void jsClick(WebElement el, WebDriver driver) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        } catch (Exception e) {
            try { el.click(); } catch (Exception ignored) {}
        }
    }

    public static void closeOverlaysIfPresent(WebDriver driver) {
        try {
            String[] selectors = {".modal-close", ".cookie-consent__close", ".close-overlay", "button[aria-label='Close']"};
            for (String sel : selectors) {
                try {
                    WebElement e = driver.findElement(By.cssSelector(sel));
                    if (e.isDisplayed()) jsClick(e, driver);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }
}
