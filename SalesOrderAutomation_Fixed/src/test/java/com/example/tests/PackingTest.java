package com.example.tests;

import com.example.pages.PackingPage;
import com.example.pages.ShipmentPage;
import org.testng.annotations.Test;

public class PackingTest extends BaseTest {

    @Test(dependsOnMethods = "com.example.tests.PicklistTest.createPicklist")
    public void pickPackAndShip() {
        if (picklistNo == null) {
            throw new IllegalStateException("Picklist number is null. Ensure PicklistTest ran successfully.");
        }

        // perform pick & save
        com.example.pages.PicklistPage pick = new com.example.pages.PicklistPage(driver);
        pick.pickAndSave(picklistNo);

        // packing
        PackingPage pack = new PackingPage(driver);
        pack.performPacking(picklistNo, "NYX", "10");

        // create shipment
        ShipmentPage ship = new ShipmentPage(driver);
        ship.createShipment(picklistNo);

        System.out.println("✅ Successfully completed flow for Picklist #: " + picklistNo);
    }
}
