package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {
    private By email = By.id("txtEmail");
    private By password = By.id("txtPassword");
    private By signIn = By.id("btnSignin");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String pwd) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(email)).clear();
        driver.findElement(email).sendKeys(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(password)).clear();
        driver.findElement(password).sendKeys(pwd);
        wait.until(ExpectedConditions.elementToBeClickable(signIn)).click();
    }
}
