package com.example.pages;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
public class SalesOrderPage extends BasePage {
	{this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));}
    private By outboundLink = By.cssSelector("#side-nav-menu > li:nth-of-type(4) > a > .menu-text");
    private By ordersLink = By.cssSelector("ul.sub-menu.show > li.menu-item > a > span.menu-text");
    private By createSOBtn = By.cssSelector("#createSOBtn > span");{
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));}
    private By customer = By.id("customer_select");{
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));}
    private By orderNumber = By.id("Ordernumber");
    private By orderType = By.id("OrderType");
    private By channel = By.id("channel-dropdown");
    private By addOItem = By.id("add-oitem-form");
    private By sku = By.id("SKUselect-so-add");
    private By qty = By.id("quantity");
    private By addItems = By.cssSelector("#btnadditems > span");
    private By createOrderBtn = By.cssSelector("#sorder-create > span");
    private By searchBox = By.cssSelector("[data-bind=\"order-tablesearch\"]");
    private By searchBtn = By.cssSelector("button.btn.search-datatable-btn");
    private By viewIcon = By.cssSelector("i.fa.fa-external-link");
    private By dropdownToggle = By.cssSelector("button.btn.dropdown-toggle");
    private By createPicklist = By.id("CreatePicklist");
    private By confirmBtn = By.cssSelector("button.modal-box-button.focused");
    private By globalShortItemsConfirm = By.cssSelector("#global_short_items_modal > div.page-foot > div.modal-box-controls > button.btn.center-btn.modal-box-button");

    public SalesOrderPage(WebDriver driver) {
        super(driver);
    }
    public String createOrder(String customerName, String skuValue, String quantity) {
        // navigate to Orders page
        wait.until(ExpectedConditions.elementToBeClickable(outboundLink)).click();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        wait.until(ExpectedConditions.elementToBeClickable(ordersLink)).click();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        wait.until(ExpectedConditions.elementToBeClickable(createSOBtn)).click();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        // fill customer
      /*  this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        wait.until(ExpectedConditions.visibilityOfElementLocated(customer)).clear();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        driver.findElement(customer).sendKeys(customerName);
        driver.findElement(customer).sendKeys(Keys.DOWN);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        driver.findElement(customer).sendKeys(Keys.ENTER);*/
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

     // Wait until the customer field is visible and clear it
     WebElement customerField = wait.until(ExpectedConditions.visibilityOfElementLocated(customer));
     customerField.clear();

     // Type the customer name
     customerField.sendKeys("Test Customer");

     // Wait for dropdown options to appear before selecting
     By dropdownOption = By.xpath("//li[contains(., '" + "Test Customer" + "')]"); // adjust locator as needed
     wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownOption));

     // Select the customer from dropdown
     customerField.sendKeys(Keys.DOWN);
     customerField.sendKeys(Keys.ENTER);


        // dynamic order number
        String orderNo = "ORDER" + System.currentTimeMillis();
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderNumber)).clear();
        driver.findElement(orderNumber).sendKeys(orderNo);

        // selects
        wait.until(ExpectedConditions.elementToBeClickable(orderType));
        new Select(driver.findElement(orderType)).selectByVisibleText("Fulfillment");
        wait.until(ExpectedConditions.elementToBeClickable(channel));
        new Select(driver.findElement(channel)).selectByVisibleText("Manual");

        // add item
        /*wait.until(ExpectedConditions.elementToBeClickable(addOItem)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(sku)).clear();
        driver.findElement(sku).sendKeys(skuValue);
        driver.findElement(sku).sendKeys(Keys.DOWN);
        driver.findElement(sku).sendKeys(Keys.ENTER);*/
        
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(50));

     // Click "Add Order Item" button
     wait1.until(ExpectedConditions.elementToBeClickable(addOItem)).click();

     // Wait until SKU field is visible and clear it
     WebElement skuField = wait1.until(ExpectedConditions.visibilityOfElementLocated(sku));
     skuField.clear();

     // Enter the SKU value
     skuField.sendKeys("NXy-Test");

     // Wait for dropdown options to appear (adjust locator as needed)
     By skuDropdownOption = By.xpath("//li[contains(., '" + "NXy-Test" + "')]"); 
     wait1.until(ExpectedConditions.visibilityOfElementLocated(skuDropdownOption));

     // Once dropdown is visible, select SKU using keyboard
     skuField.sendKeys(Keys.DOWN);
     skuField.sendKeys(Keys.ENTER);


        wait1.until(ExpectedConditions.visibilityOfElementLocated(qty)).clear();
        driver.findElement(qty).sendKeys(quantity);

        wait1.until(ExpectedConditions.elementToBeClickable(addItems)).click();
        wait1.until(ExpectedConditions.elementToBeClickable(createOrderBtn)).click();

        return orderNo;
    }

    public void searchOrder(String orderNo) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox)).clear();
        driver.findElement(searchBox).sendKeys(orderNo);
        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();
    }

    public void createPicklistFromSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(viewIcon)).click();
        wait.until(ExpectedConditions.elementToBeClickable(dropdownToggle)).click();
        wait.until(ExpectedConditions.elementToBeClickable(createPicklist)).click();
        wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
        // handle global short items modal if appears
        try {
            wait.until(ExpectedConditions.elementToBeClickable(globalShortItemsConfirm)).click();
        } catch (Exception ignored) {}
    }
}
