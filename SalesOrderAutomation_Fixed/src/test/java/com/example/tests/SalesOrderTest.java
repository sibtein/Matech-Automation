package com.example.tests;

import com.example.pages.SalesOrderPage;

import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class SalesOrderTest extends BaseTest {

    @Test(dependsOnMethods = "com.example.tests.LoginTest.login")
    public void createOrder() {
        SalesOrderPage so = new SalesOrderPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        orderNo = so.createOrder("Test Customer", "NXy-Test", "10");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        System.out.println("✅ Created Sales Order: " + orderNo);
    }
}
