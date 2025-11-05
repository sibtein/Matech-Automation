package com.example.tests;

import com.example.pages.PicklistPage;
import com.example.pages.SalesOrderPage;
import org.testng.annotations.Test;

public class PicklistTest extends BaseTest {

    @Test(dependsOnMethods = "com.example.tests.SalesOrderTest.createOrder")
    public void createPicklist() {
        SalesOrderPage so = new SalesOrderPage(driver);
        // use shared orderNo from BaseTest
        if (orderNo == null) {
            throw new IllegalStateException("Order number is null. Ensure SalesOrderTest ran successfully.");
        }
        so.searchOrder(orderNo);
        so.createPicklistFromSearch();
        PicklistPage pl = new PicklistPage(driver);
        picklistNo = pl.capturePicklistNumber();
        if (picklistNo == null) {
           // picklistNo = "5929";
        }
        System.out.println("📦 Created Picklist #: " + picklistNo);
    }
}
