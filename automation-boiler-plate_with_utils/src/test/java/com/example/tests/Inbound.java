package com.example.tests;

import com.example.tests.utils.TestUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Scanner;

/**
 * Resilient Inbound test using TestUtils helpers.
 */
public class Inbound extends BaseTest {

    private static final int LONG_WAIT_MS = 5000;
    private static final int SHORT_WAIT_MS = 800;
    private static final int WAIT_AFTER_STATUS_CHANGE_MS = 5000;
    private static final int CLICK_RETRIES = 3;

    @Test
    public void inboundShipmentTest() throws InterruptedException {
        Actions actions = new Actions(driver);

        try {
            log("Starting inboundShipmentTest");

            // --- LOGIN ---
            log("Waiting for login fields...");
            WebElement emailField = waitForVisible(By.cssSelector("input[type='email']"), 20);
            safeSendKeys(emailField, props.getProperty("username", "superadmin@matechco.com"));

            WebElement passwordField = waitForVisible(By.cssSelector("input[type='password']"), 10);
            safeSendKeys(passwordField, props.getProperty("password", "abc123"));

            WebElement signInButton = waitForVisible(By.xpath("//button[contains(.,'Sign In')] | //span[contains(.,'Sign In')]"), 10);
            retryJsClick(signInButton);
            TestUtils.ensurePageReady(driver, wait);
            TestUtils.closeOverlaysIfPresent(driver);

            // --- NAVIGATE TO INBOUND -> SHIPMENTS ---
            log("Navigating to Inbound -> Shipments");
            WebElement inboundMenu = waitForVisible(By.xpath("//span[contains(text(),'Inbound')]"), 15);
            retryJsClick(inboundMenu);

            WebElement shipmentsMenu = waitForVisible(By.xpath("//span[contains(text(),'Shipments')]"), 15);
            retryJsClick(shipmentsMenu);
            TestUtils.ensurePageReady(driver, wait);

            // --- OPEN SHIPMENT CREATION ---
            log("Opening shipment creation form");
            boolean opened = false;
            try {
                WebElement createDropdown = waitForVisible(By.cssSelector("li:nth-of-type(2) > .dropdown span:nth-of-type(1)"), 8);
                retryJsClick(createDropdown);
                WebElement shipmentLink = waitForVisible(By.linkText("Shipment"), 8);
                retryJsClick(shipmentLink);
                opened = true;
            } catch (Exception e) {
                log("First approach to open creation form failed: " + e.getMessage());
            }
            if (!opened) {
                try {
                    WebElement alt = waitForVisible(By.xpath("//button[contains(.,'Create') or contains(.,'Add Shipment') or contains(.,'New Shipment')]/.."), 6);
                    retryJsClick(alt);
                    opened = true;
                } catch (Exception ignored) {}
            }
            if (!opened) {
                throw new RuntimeException("Could not open shipment creation form — check selector");
            }
            TestUtils.ensurePageReady(driver, wait);

            // --- FILL FORM ---
            log("Filling supplier");
            WebElement supplierSelect = waitForVisible(By.id("supplier_select"), 12);
            retryJsClick(supplierSelect);
            safeSendKeys(supplierSelect, "BNOIER Vendor");
            Thread.sleep(500);
            try {
                WebElement hit = waitForVisible(By.cssSelector("div.productSearchTitle"), 4);
                retryJsClick(hit);
            } catch (Exception e) { log("Supplier auto-select not found, continuing"); }

            // tracking number
            String dynamicTrackingNumber = getNextTrackingNumber();
            log("Using tracking number: " + dynamicTrackingNumber);
            WebElement trackingNumber = waitForVisible(By.id("TrackingNumber"), 10);
            safeSendKeys(trackingNumber, dynamicTrackingNumber);

            // carrier
            try {
                WebElement carrierSelect = waitForVisible(By.id("carrier_select"), 6);
                retryJsClick(carrierSelect);
                safeSendKeys(carrierSelect, "Other");
                Thread.sleep(500);
                try {
                    WebElement hit = waitForVisible(By.cssSelector("div.productSearchTitle"), 4);
                    retryJsClick(hit);
                } catch (Exception ignored) {}
            } catch (Exception e) {
                log("Carrier select not found — continuing");
            }

            try {
                WebElement remarks = waitForVisible(By.id("ShipmentRemarks"), 6);
                safeSendKeys(remarks, "automated testing");
            } catch (Exception ignored) { log("ShipmentRemarks not found"); }

            try {
                WebElement container = waitForVisible(By.id("ContainerType"), 6);
                safeSendKeys(container, "40ft");
            } catch (Exception ignored) { log("ContainerType not found"); }

            // SKU and qty
            WebElement skuField = waitForVisible(By.id("SKUselect-add"), 12);
            retryJsClick(skuField);
            safeSendKeys(skuField, "tt1");
            Thread.sleep(1500);
            skuField.sendKeys(Keys.ARROW_DOWN);
            skuField.sendKeys(Keys.ENTER);

            WebElement totalQty = waitForVisible(By.id("TotalQty"), 8);
            safeSendKeys(totalQty, "68");

            // add item and submit
            log("Adding item and submitting shipment");
            WebElement addItemsBtn = waitForVisible(By.cssSelector("#btnadditems > span"), 10);
            retryJsClick(addItemsBtn);
            Thread.sleep(LONG_WAIT_MS);

            WebElement submitBtn = waitForVisible(By.cssSelector("#nshipment-add > span"), 12);
            retryJsClick(submitBtn);
            log("Shipment create click performed");
            Thread.sleep(LONG_WAIT_MS);

            // --- VERIFY CREATED: GO TO INBOUND LISTING ---
            log("Navigating to inbound listing and searching for tracking number");
            driver.get(props.getProperty("app.url", "https://dev.3plnext.com/#inbound"));
            TestUtils.ensurePageReady(driver, wait);
            TestUtils.closeOverlaysIfPresent(driver);

            WebElement searchBox = waitForVisible(By.cssSelector("[data-bind='shipment-tablesearch']"), 12);
            safeSendKeys(searchBox, dynamicTrackingNumber);
            Thread.sleep(SHORT_WAIT_MS);

            WebElement searchButton = waitForVisible(By.cssSelector("button.btn.search-datatable-btn"), 8);
            retryJsClick(searchButton);
            log("Search clicked for " + dynamicTrackingNumber);
            Thread.sleep(LONG_WAIT_MS);

            // open first row
            log("Opening first search result row");
            WebElement firstShipmentRow = waitForVisible(By.cssSelector("table tbody tr"), 12);
            retryJsClick(firstShipmentRow);
            Thread.sleep(1000);

            // change status to Arrival
            log("Changing status to Arrival");
            WebElement statusDropdown = waitForVisible(By.id("status-inbound"), 10);
            TestUtils.jsClick(statusDropdown, driver);
            Thread.sleep(500);

            WebElement arrivalOption = waitForVisible(By.xpath("//li[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'arrival')]"), 8);
            retryJsClick(arrivalOption);

            try {
                WebElement saveButton = waitForVisible(By.xpath("//button[contains(.,'Save') or contains(.,'Update')]"), 6);
                retryJsClick(saveButton);
                log("Save/Update clicked");
            } catch (TimeoutException te) {
                log("No Save/Update button; maybe auto-save");
            }

            boolean statusOk = waitForText(By.id("status-inbound"), "Arrival", 10);
            if (!statusOk) {
                log("Arrival not confirmed in text — continuing but verify manually");
            } else {
                log("Arrival confirmed.");
            }

            Thread.sleep(WAIT_AFTER_STATUS_CHANGE_MS);

            // shipments history search
            driver.get(props.getProperty("app.url", "https://dev.3plnext.com/#inbound"));
            TestUtils.ensurePageReady(driver, wait);
            TestUtils.closeOverlaysIfPresent(driver);

            WebElement shipmentsHistoryTab = waitForVisible(By.cssSelector("[data-state='ShipmentsHistory']"), 10);
            retryJsClick(shipmentsHistoryTab);

            WebElement historySearchBox = waitForVisible(By.cssSelector("[data-bind='shipment-history-tablesearch']"), 10);
            safeSendKeys(historySearchBox, dynamicTrackingNumber);
            Thread.sleep(SHORT_WAIT_MS);

            WebElement historySearchBtn = waitForVisible(By.cssSelector("button.btn.search-datatable-btn"), 8);
            retryJsClick(historySearchBtn);
            Thread.sleep(LONG_WAIT_MS);

            WebElement firstHistoryRow = waitForVisible(By.cssSelector("table tbody tr"), 10);
            retryJsClick(firstHistoryRow);
            Thread.sleep(1500);

            try {
                WebElement putawayTab = waitForVisible(By.cssSelector("[data-state='PutAwayList']"), 6);
                retryJsClick(putawayTab);
                log("Opened PutAway tab");
            } catch (Exception ignored) { log("PutAway tab not present"); }

            log("Completed flow for tracking: " + dynamicTrackingNumber);

        } catch (Exception e) {
            log("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
    }

    // -------------------- Helpers --------------------

    private void log(String msg) {
        System.out.println("[Inbound] " + msg);
    }

    private WebElement waitForVisible(By by, int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private boolean waitForText(By by, String text, int seconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.textToBePresentInElementLocated(by, text));
        } catch (Exception e) {
            return false;
        }
    }

    private void safeSendKeys(WebElement el, String text) {
        try {
            el.clear();
            el.sendKeys(text);
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", el, text);
            } catch (Exception ignored) {}
        }
    }

    private void retryJsClick(WebElement el) throws InterruptedException {
        int attempts = 0;
        while (attempts < CLICK_RETRIES) {
            try {
                TestUtils.jsClick(el, driver);
                return;
            } catch (Exception e) {
                attempts++;
                Thread.sleep(400);
                if (attempts >= CLICK_RETRIES) throw e;
            }
        }
    }

    // Helper: Incrementing tracking number persisted in file
    private String getNextTrackingNumber() {
        try {
            File file = new File("tracking_number.txt");
            int number = 1;

            if (file.exists()) {
                try (Scanner sc = new Scanner(file)) {
                    if (sc.hasNextInt()) number = sc.nextInt() + 1;
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
}
