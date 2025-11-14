
package com.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static Properties props = new Properties();

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws Exception {
        // load config if present
        try (InputStream is = new FileInputStream("src/main/resources/config.properties")) {
            props.load(is);
        } catch (Exception ignored) {}

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // non-headless for debugging; remove args for headless CI
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);

        // timeouts and window
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        try { driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60)); } catch (Exception ignored) {}
        try { driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30)); } catch (Exception ignored) {}
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try { driver.manage().window().maximize(); } catch (Exception ignored) {}
        try { ((RemoteWebDriver) driver).manage().window().setSize(new org.openqa.selenium.Dimension(1366,768)); } catch (Exception ignored) {}
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
