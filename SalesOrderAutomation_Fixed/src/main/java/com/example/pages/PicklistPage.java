package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PicklistPage extends BasePage {

    public PicklistPage(WebDriver driver) {
        super(driver);
        wait.withTimeout(Duration.ofSeconds(15)); // handle slower modals
    }

    /*
     * Capture the generated picklist number from success popup text like:
     * "Picklist Created Successfully with PickList # 2202500005948."
     */
    public String capturePicklistNumber() {
        String picklistNumber = null;

        try {
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Picklist Created') or contains(.,'PickList #')]")
            ));

            String modalText = modal.getText().trim();
            System.out.println("📋 Modal Text: " + modalText);

            Matcher matcher = Pattern.compile("(\\d{8,15})").matcher(modalText);
            if (matcher.find()) {
                picklistNumber = matcher.group(1);
                System.out.println("✅ Captured Picklist #: " + picklistNumber);
            } else {
                System.out.println("⚠️ Picklist number not found in modal text!");
            }

            try {
                WebElement viewBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'View Picklist')]")
                ));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", viewBtn);
                viewBtn.click();
                System.out.println("👁️ Opened runtime Picklist page.");
            } catch (Exception e) {
                System.out.println("⚠️ 'View Picklist' button not found or not clickable: " + e.getMessage());
            }

        } catch (TimeoutException te) {
            System.out.println("❌ Timeout: No success popup appeared.");
        } catch (Exception e) {
            System.out.println("❌ Error capturing Picklist number: " + e.getMessage());
        }

        return picklistNumber;
    }

    /**
     * Perform manual picking steps for the given runtime picklist number.
     */
    public void pickAndSave(String picklistId) {
        System.out.println("➡️ Performing manual picking for Picklist: " + picklistId);

        try {
            // Navigate directly to the Manual Picking page for this runtime picklist
            driver.get("https://dev.3plnext.com/#Outbound/ManualPicking?id=" + picklistId);

            // Step 1: Click Receive button
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnReceive > span"))).click();
            System.out.println("✅ Clicked Receive.");

            // Step 2: Click Save button
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnSave > span"))).click();
            System.out.println("✅ Clicked Save.");

            // Step 3: Confirm modal if present
            try {
                WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#production_lane_modal .modal-box-button")
                ));
                confirmBtn.click();
                System.out.println("✅ Confirmed modal after save.");
            } catch (TimeoutException te) {
                System.out.println("⚠️ No confirmation modal appeared.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error during manual picking: " + e.getMessage());
        }
    }

    /**
     * Opens a picklist by searching its ID in the Picklist table.
     */
    public void openPicklist(String picklistId) {
        System.out.println("➡️ Opening Picklist: " + picklistId);
        try {
            driver.get("https://dev.3plnext.com/#Outbound/Picklist");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

            try {
                WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[type='search'], input.form-control")
                ));
                searchBox.clear();
                searchBox.sendKeys(picklistId);
                searchBox.sendKeys(Keys.ENTER);
                Thread.sleep(1000);
            } catch (Exception ignored) {}

            try {
                WebElement row = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//tr[td[contains(text(),'" + picklistId + "')]]")
                ));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", row);
                row.click();
            } catch (Exception e) {
                System.out.println("⚠️ Could not click picklist row: " + e.getMessage());
            }

            try {
                WebElement openBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//tr[td[contains(text(),'" + picklistId + "')]]//a[contains(.,'Open') or contains(.,'View')]")
                ));
                openBtn.click();
                System.out.println("✅ Opened Picklist: " + picklistId);
            } catch (Exception e) {
                System.out.println("⚠️ Could not open picklist details: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to open Picklist: " + e.getMessage());
        }
    }

    /**
     * Capture picklist number and perform picking in one go.
     */
    public String captureAndPick() {
        String picklistId = capturePicklistNumber();
        if (picklistId != null && !picklistId.isEmpty()) {
            pickAndSave(picklistId);
            return picklistId;
        } else {
            System.out.println("⚠️ No picklist number captured.");
            return null;
        }
    }
}
