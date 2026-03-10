package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
class OrderFunctionalTest {

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
    void createOrderPage_isReachable(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(baseUrl + "/order/create");

        WebElement heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        String pageTitle = driver.getTitle();

        assertTrue(pageTitle.contains("Order"));
        assertTrue(heading.getText().toLowerCase().contains("order"));
    }

    @Test
    void historyPage_canSearchByAuthor(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(baseUrl + "/order/history");

        WebElement authorInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("authorSearchInput")));
        authorInput.clear();
        authorInput.sendKeys("Some Author");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // After submitting, we should still be on the history page and not see an error
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Order History"));
    }
}

