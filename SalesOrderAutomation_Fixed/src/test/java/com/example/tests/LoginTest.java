package com.example.tests;

import com.example.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void login() {
        driver.get(config.getProperty("baseUrl"));
        new LoginPage(driver).login(config.getProperty("username"), config.getProperty("password"));
        System.out.println("✅ Logged in as: " + config.getProperty("username"));
    }
}
