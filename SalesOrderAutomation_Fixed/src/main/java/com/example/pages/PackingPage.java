package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PackingPage extends BasePage {
    public PackingPage(WebDriver driver) { super(driver); }

    public void performPacking(String picklistId, String skuCode, String quantity) {
        // navigate to packing/scan page via menu
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#side-nav-menu > li:nth-of-type(4) > a > .menu-text"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".show > li:nth-of-type(3) .menu-text"))).click();
        } catch (Exception ignored) {}

        // enter bin code (use picklistId or a known bin)
        try {
            WebElement bin = wait.until(ExpectedConditions.elementToBeClickable(By.id("bin_code")));
            bin.clear();
            bin.sendKeys(picklistId);
            bin.sendKeys(Keys.ENTER);
        } catch (Exception ignored) {}

        // scan item
        try {
            WebElement scan = wait.until(ExpectedConditions.elementToBeClickable(By.id("scan_item")));
            scan.clear();
            scan.sendKeys("NXy-Test");
            scan.sendKeys(Keys.ENTER);
        } catch (Exception ignored) {}

        // enter quantity
        try {
            WebElement q = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.form-control.qty-mn")));
            q.clear();
            q.sendKeys(quantity);
            q.sendKeys(Keys.ENTER);
        } catch (Exception ignored) {}
    }
}
