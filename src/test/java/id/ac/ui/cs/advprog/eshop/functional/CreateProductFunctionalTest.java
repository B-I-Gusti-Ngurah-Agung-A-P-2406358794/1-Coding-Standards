package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class CreateProductFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void createProduct_userCanSeeNewProductInList(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Go to create page
        driver.get(baseUrl + "/product/create");

        // 2. Fill form with unique data
        String uniqueName = "Selenium-Type-" + System.currentTimeMillis();
        String qty = "25";

        // Use wait before finding elements to handle slow initial loads
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameInput")));
        nameInput.clear();
        nameInput.sendKeys(uniqueName);

        WebElement qtyInput = driver.findElement(By.id("quantityInput"));
        qtyInput.clear();
        qtyInput.sendKeys(qty);

        // 3. Submit and wait for the redirect to happen naturally
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for URL to change to the list page
        wait.until(ExpectedConditions.urlContains("/product/list"));

        // 4. Verify product appears in the list
        // We wait for the specific text to be present in the body
        WebElement body = driver.findElement(By.tagName("body"));
        wait.until(ExpectedConditions.textToBePresentInElement(body, uniqueName));
        wait.until(ExpectedConditions.textToBePresentInElement(body, qty));

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(uniqueName), "Product name not found in list!");
        assertTrue(pageSource.contains(qty), "Product quantity not found in list!");
    }
}