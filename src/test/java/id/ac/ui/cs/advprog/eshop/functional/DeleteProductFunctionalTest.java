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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String productName = "DeleteMe-" + System.currentTimeMillis();

        // 1) Create a product
        driver.get(baseUrl + "/product/create");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameInput"))).sendKeys(productName);
        driver.findElement(By.id("quantityInput")).sendKeys("5");

        // Click and wait for the URL to change to the list page automatically
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/product/list"));

        // 2) Find the row using a more specific TD-based XPath
        // This looks for a <td> containing our text, then goes up to the parent <tr>
        String rowXpath = String.format("//td[contains(text(), '%s')]/parent::tr", productName);
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(rowXpath)));

        // 3) Find the delete button within that row.
        // Using contains(@class, 'btn-danger') is safer than a direct class match.
        WebElement deleteButton = row.findElement(By.xpath(".//button[contains(@class, 'btn-danger')] | .//a[contains(@class, 'btn-danger')]"));
        deleteButton.click();

        // 4) Handle Alert
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // 5) Verify deletion
        // We wait for the specific product name to vanish from the body text
        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), productName)
        ));

        assertFalse(driver.getPageSource().contains(productName), "Product still exists in page source!");
    }
}