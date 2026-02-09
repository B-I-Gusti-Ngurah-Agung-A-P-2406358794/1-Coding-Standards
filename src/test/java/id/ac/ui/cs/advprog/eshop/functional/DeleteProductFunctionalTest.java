package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class DeleteProductFunctionalTest {

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
    void deleteProduct_userCanDeleteAndProductDisappearsFromList(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // 1) Create a product first
        driver.get(baseUrl + "/product/create");
        driver.findElement(By.id("nameInput")).sendKeys("Product To Delete");
        WebElement qtyInput = driver.findElement(By.id("quantityInput"));
        qtyInput.clear();
        qtyInput.sendKeys("5");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 2) Open list page
        driver.get(baseUrl + "/product/list");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("tbody")));

        // 3) Find the row and click Delete button in that row
        WebElement row = driver.findElement(By.xpath("//tr[td[contains(text(),'Product To Delete')]]"));
        row.findElement(By.cssSelector("button.btn-danger")).click();

        // 4) Confirm the alert popup
        driver.switchTo().alert().accept();

        // 5) Reload list and confirm it is gone
        driver.get(baseUrl + "/product/list");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("tbody")));

        String pageSource = driver.getPageSource();
        assertFalse(pageSource.contains("Product To Delete"));

    }
}
