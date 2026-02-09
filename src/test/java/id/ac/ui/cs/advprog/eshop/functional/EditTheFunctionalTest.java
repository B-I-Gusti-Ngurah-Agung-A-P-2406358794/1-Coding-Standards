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
class EditTheFunctionalTest {

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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String originalName = "ProductToEdit-" + System.currentTimeMillis();
        String editedName = "Edited-" + System.currentTimeMillis();

        // 1) Create product via UI (Instead of Service) to ensure it's in the server context
        driver.get(baseUrl + "/product/create");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nameInput"))).sendKeys(originalName);
        driver.findElement(By.id("quantityInput")).sendKeys("10");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 2) Go to list and wait until product appears
        wait.until(ExpectedConditions.urlContains("/product/list"));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), originalName));

        // 3) Find the edit link.
        // We use a broader XPath to find the link that is in the same row as our originalName
        WebElement editLink = driver.findElement(By.xpath("//tr[td[contains(text(),'" + originalName + "')]]//a[contains(@href, 'edit')]"));
        editLink.click();

        // 4) Edit page: update values
        // Check if your HTML uses id="nameInput" or name="productName" and adjust accordingly
        WebElement editNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameInput")));
        editNameInput.clear();
        editNameInput.sendKeys(editedName);

        WebElement editQtyInput = driver.findElement(By.id("quantityInput"));
        editQtyInput.clear();
        editQtyInput.sendKeys("99");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 5) Verify updated result in list
        wait.until(ExpectedConditions.urlContains("/product/list"));

        // Wait for the new name to actually appear in the DOM
        assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), editedName)));
        assertTrue(driver.getPageSource().contains("99"));
    }
}