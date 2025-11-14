package com.example.tests;

import com.example.tests.utils.TestUtils;
import org.testng.annotations.Test;

/**
 * Outbound test class with helper methods and TestNG tests.
 */
public class Outbound extends BaseTest {
    private static String orderNo;
    private static String picklistNo;

    // Helper methods (placeholders — replace with real interactions)
    public void login(String user, String pass) {
        System.out.println("[Outbound] login with " + user);
        driver.get(props.getProperty("app.url", "about:blank"));
        TestUtils.ensurePageReady(driver, wait);
        TestUtils.closeOverlaysIfPresent(driver);
    }

    public String createOrder(String customer, String site, String qty) {
        System.out.println("[Outbound] createOrder for " + customer);
        return "SO-" + System.currentTimeMillis() % 100000;
    }

    public String createPicklist(String orderNo) {
        System.out.println("[Outbound] createPicklist for " + orderNo);
        return "PL-" + System.currentTimeMillis() % 100000;
    }

    public void performPacking(String picklistNo, String site, String qty) {
        System.out.println("[Outbound] performPacking for " + picklistNo);
    }

    public void createShipment(String picklistNo) {
        System.out.println("[Outbound] createShipment for " + picklistNo);
    }

    // Tests
    @Test(groups = {"Smoke"}, priority = 1)
    public void loginTest() {
        login(props.getProperty("app.user", "demo_user"), props.getProperty("app.pass", "demo_pass"));
        System.out.println("✅ Login test executed");
    }

    @Test(groups = {"Smoke"}, priority = 2, dependsOnMethods = "loginTest")
    public void createOrderTest() {
        orderNo = createOrder("AutoCustomer", "NYX", "10");
        System.out.println("✅ Created Sales Order: " + orderNo);
    }

    @Test(groups = {"Regression"}, priority = 3, dependsOnMethods = "createOrderTest")
    public void createPicklistTest() {
        picklistNo = createPicklist(orderNo);
        System.out.println("✅ Created Picklist: " + picklistNo);
    }

    @Test(groups = {"Regression"}, priority = 4, dependsOnMethods = "createPicklistTest")
    public void performPackingTest() {
        performPacking(picklistNo, "NYX", "10");
        System.out.println("✅ Packing done for: " + picklistNo);
    }

    @Test(groups = {"Regression"}, priority = 5, dependsOnMethods = "performPackingTest")
    public void createShipmentTest() {
        createShipment(picklistNo);
        System.out.println("✅ Shipment created for: " + picklistNo);
    }
}
