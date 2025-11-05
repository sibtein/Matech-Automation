package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ShipmentPage extends BasePage {
    public ShipmentPage(WebDriver driver) { super(driver); }

    public void createShipment(String picklistId) {
        // try to create shipment from current context
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("create-shipment"))).click();
        } catch (Exception e) {
            try { wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnCreateShipment, button#create-shipment, #create-shipment"))).click(); } catch (Exception ignored) {}
        }
        // confirm modal and save
        try { wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#_modal_block_ui .modal-box-button"))).click(); } catch (Exception ignored) {}
        try { wait.until(ExpectedConditions.elementToBeClickable(By.id("save-shipment"))).click(); } catch (Exception ignored) {}
    }
}
