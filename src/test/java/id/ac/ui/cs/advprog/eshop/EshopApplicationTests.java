package id.ac.ui.cs.advprog.eshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class EshopApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty: test passes if Spring context starts successfully.
    }

    @Test
    void main_runsWithoutThrowing() {
        assertDoesNotThrow(() -> EshopApplication.main(new String[]{}));
    }
}
