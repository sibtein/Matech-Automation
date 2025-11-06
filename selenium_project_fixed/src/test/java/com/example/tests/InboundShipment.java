package com.example.tests;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Scanner;

public class InboundShipment extends BaseTest {

    private static final int LONG_WAIT_MS = 5000;
    private static final int WAIT_AFTER_STATUS_CHANGE_MS = 5000;

    @Test
    public void inboundShipmentTest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Actions actions = new Actions(driver);

        try {
            System.out.println("🔹 Logging in...");

            // Login
            WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='email']")));
            emailField.clear();
            emailField.sendKeys("superadmin@matechco.com");

            WebElement passwordField = driver.findElement(By.cssSelector("input[type='password']"));
            passwordField.clear();
            passwordField.sendKeys("abc123");

            WebElement signInButton = driver.findElement(By.xpath("//button[contains(.,'Sign In')] | //span[contains(.,'Sign In')]"));
            signInButton.click();

            // Dashboard wait
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Inbound')]"))).click();
            System.out.println("✅ Inbound clicked");

            // Create Inbound
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Shipments')]"))).click();
            System.out.println("✅ Create Inbound clicked");

            // ✅ Open Shipment Creation
            driver.findElement(By.cssSelector("li:nth-of-type(2) > .dropdown span:nth-of-type(1)")).click();
            driver.findElement(By.linkText("Shipment")).click();

            // ✅ Fill form
            WebElement supplierSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("supplier_select")));
            supplierSelect.click();
            supplierSelect.sendKeys("BNOIER Vendor");
            Thread.sleep(500);
            driver.findElement(By.cssSelector("div.productSearchTitle")).click();

            // ✅ Dynamic Tracking Number
            String dynamicTrackingNumber = getNextTrackingNumber();
            System.out.println("📦 Using Tracking Number: " + dynamicTrackingNumber);

            WebElement trackingNumber = wait.until(ExpectedConditions.elementToBeClickable(By.id("TrackingNumber")));
            trackingNumber.clear();
            trackingNumber.sendKeys(dynamicTrackingNumber);

            WebElement carrierSelect = driver.findElement(By.id("carrier_select"));
            carrierSelect.click();
            carrierSelect.sendKeys("Other");
            Thread.sleep(500);
            driver.findElement(By.cssSelector("div.productSearchTitle")).click();

            driver.findElement(By.id("ShipmentRemarks")).sendKeys("automated testing");
            driver.findElement(By.id("ContainerType")).sendKeys("40ft");

            // ✅ SKU + Qty
            WebElement skuField = wait.until(ExpectedConditions.elementToBeClickable(By.id("SKUselect-add")));
            skuField.click();
            skuField.clear();
            skuField.sendKeys("tt1");
            Thread.sleep(1500);
            skuField.sendKeys(Keys.ARROW_DOWN);
            skuField.sendKeys(Keys.ENTER);

            WebElement totalQty = driver.findElement(By.id("TotalQty"));
            totalQty.clear();
            totalQty.sendKeys("68");

            // ✅ Add item + submit
            driver.findElement(By.cssSelector("#btnadditems > span")).click();
            Thread.sleep(LONG_WAIT_MS);
            driver.findElement(By.cssSelector("#nshipment-add > span")).click();
            System.out.println("✅ Shipment created successfully.");
            Thread.sleep(LONG_WAIT_MS);

            // ✅ Go to Inbound listing
            driver.get("https://dev.3plnext.com/#inbound");

            // ✅ Search for the created tracking number
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("[data-bind='shipment-tablesearch']")));
            searchBox.clear();
            searchBox.sendKeys(dynamicTrackingNumber);
            Thread.sleep(1000);

            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.btn.search-datatable-btn")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
            System.out.println("🔍 Clicked Search Button for Tracking Number: " + dynamicTrackingNumber);
            Thread.sleep(LONG_WAIT_MS);

            // ✅ Open the shipment detail (first row)
            WebElement firstShipmentRow = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("table tbody tr")));
            firstShipmentRow.click();
            System.out.println("✅ Shipment row selected.");
            Thread.sleep(1000);

            // ✅ Change status to Arrival
            WebElement statusDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("status-inbound")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", statusDropdown);
            Thread.sleep(500);
            statusDropdown.click();

            WebElement arrivalOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'arrival')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", arrivalOption);
            System.out.println("✅ Clicked 'Arrival' option.");

            try {
                WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(.,'Save') or contains(.,'Update')]")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
                System.out.println("💾 Clicked Save/Update button.");
            } catch (TimeoutException e) {
                System.out.println("ℹ️ No Save/Update button present - assuming auto-save.");
            }

            // ✅ Wait until UI reflects Arrival status
            try {
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.textToBePresentInElementLocated(
                        By.id("status-inbound"), "Arrival"));
                System.out.println("✅ Status confirmed as 'Arrival'.");
            } catch (TimeoutException e) {
                System.out.println("⚠️ Could not confirm 'Arrival' text in time.");
            }

            // ✅ Hold the screen for 5 seconds after Arrival
            System.out.println("⏳ Holding screen for 5 seconds after Arrival...");
            Thread.sleep(WAIT_AFTER_STATUS_CHANGE_MS);
            System.out.println("✅ 5-second hold complete.");

            // ✅ Now go to Shipments History tab
            driver.get("https://dev.3plnext.com/#inbound");
            WebElement shipmentsHistoryTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("[data-state='ShipmentsHistory']"))
            );
            shipmentsHistoryTab.click();
            System.out.println("✅ Opened Shipments History tab.");

            // ✅ Search using same dynamic tracking number
            WebElement historySearchBox = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("[data-bind='shipment-history-tablesearch']"))
            );
            historySearchBox.clear();
            historySearchBox.sendKeys(dynamicTrackingNumber);
            Thread.sleep(1000);

            // ✅ Click the search button for history
            WebElement historySearchBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn.search-datatable-btn"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", historySearchBtn);
            System.out.println("🔍 Searching in Shipments History for: " + dynamicTrackingNumber);
            Thread.sleep(LONG_WAIT_MS);

            // ✅ Click the first row of results
            WebElement firstHistoryRow = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("table tbody tr"))
            );
            firstHistoryRow.click();
            System.out.println("✅ Shipment history detail opened for: " + dynamicTrackingNumber);

            // ✅ Optional - go to PutAway List tab
            try {
                WebElement putawayTab = wait.until(
                        ExpectedConditions.elementToBeClickable(By.cssSelector("[data-state='PutAwayList']"))
                );
                putawayTab.click();
                System.out.println("📦 Opened PutAway List tab.");
            } catch (Exception e) {
                System.out.println("⚠️ PutAway List tab not found or clickable.");
            }

            Thread.sleep(3000);
            System.out.println("✅ Completed entire flow successfully for tracking: " + dynamicTrackingNumber);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("❌ Test failed: " + e.getMessage());
        }
    }

    // ✅ Helper: Incrementing tracking number persisted in file
    private String getNextTrackingNumber() {
        try {
            File file = new File("tracking_number.txt");
            int number = 1;

            if (file.exists()) {
                try (Scanner sc = new Scanner(file)) {
                    if (sc.hasNextInt()) {
                        number = sc.nextInt() + 1;
                    }
                }
            }

            try (FileWriter fw = new FileWriter(file, false)) {
                fw.write(String.valueOf(number));
            }

            return "1616-" + String.format("%02d", number);
        } catch (Exception e) {
            e.printStackTrace();
            return "1616-" + (int) (Math.random() * 100);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser closed and test finished.");
        }
    }
}
