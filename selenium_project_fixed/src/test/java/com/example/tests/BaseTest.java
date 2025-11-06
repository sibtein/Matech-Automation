package com.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {
    protected WebDriver driver;
    protected Properties config;

    @BeforeClass
    public void setUp() {
        loadConfig();
        String browser = config.getProperty("browser", "chrome").toLowerCase();
        String headless = config.getProperty("headless", "no").toLowerCase();

        System.out.println("🚀 Launching tests on browser: " + browser + 
                           (headless.equals("yes") ? " (headless)" : ""));

        try {
            switch (browser) {
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (headless.equals("yes")) {
                        edgeOptions.addArguments("--headless=new", "--disable-gpu");
                    }
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver(edgeOptions);
                    break;

                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (headless.equals("yes")) {
                        chromeOptions.addArguments("--headless", "--disable-gpu");
                    }
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            String baseUrl = config.getProperty("baseUrl");
            System.out.println("🌐 Navigating to: " + baseUrl);
            driver.get(baseUrl);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Browser launch failed.", e);
        }
    }

    private void loadConfig() {
        config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("❌ config.properties not found in resources!");
            }
            config.load(input);
            System.out.println("✅ Loaded config.properties successfully");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to load config.properties.", e);
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser closed successfully.");
        }
    }
}
