package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class EditProductFunctionalTest {

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
    void editProduct_userCanEditAndSeeUpdatedInList(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        String originalName = "ProductToEdit-" + System.currentTimeMillis();
        String editedName = "Edited-" + System.currentTimeMillis();

        // Create product
        driver.get(baseUrl + "/product/create");
        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nameInput")));
        nameInput.clear();
        nameInput.sendKeys(originalName);

        WebElement qtyInput = driver.findElement(By.id("quantityInput"));
        qtyInput.clear();
        qtyInput.sendKeys("10");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Go to list and wait for the row
        driver.get(baseUrl + "/product/list");
        By rowLocator = By.xpath("//tr[td[contains(normalize-space(.),'" + originalName + "')]]");
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(rowLocator));

        // Click edit link by href
        row.findElement(By.cssSelector("a[href*='/product/edit/']")).click();

        // Edit page: update values
        WebElement editNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nameInput")));
        editNameInput.clear();
        editNameInput.sendKeys(editedName);

        WebElement editQtyInput = driver.findElement(By.id("quantityInput"));
        editQtyInput.clear();
        editQtyInput.sendKeys("99");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verify in list
        driver.get(baseUrl + "/product/list");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("tbody")));

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(editedName));
        assertTrue(pageSource.contains("99"));
    }
}
