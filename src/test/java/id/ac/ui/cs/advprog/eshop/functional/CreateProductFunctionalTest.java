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
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Go to create page
        driver.get(baseUrl + "/product/create");
        assertEquals("Create New Product", driver.getTitle());

        // Fill form
        String name = "Test Product Selenium";
        String qty = "25";

        WebElement nameInput = driver.findElement(By.id("nameInput"));
        nameInput.clear();
        nameInput.sendKeys(name);

        WebElement qtyInput = driver.findElement(By.id("quantityInput"));
        qtyInput.clear();
        qtyInput.sendKeys(qty);

        // Submit
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Make sure list page is loaded (either via redirect or manual open)
        driver.get(baseUrl + "/product/list");

        // Wait until table body exists
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("tbody")));

        // Verify product appears
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(name));
        assertTrue(pageSource.contains(qty));
    }
}
