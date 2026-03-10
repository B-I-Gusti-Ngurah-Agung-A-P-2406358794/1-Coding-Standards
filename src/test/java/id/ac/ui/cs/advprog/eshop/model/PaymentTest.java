package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PaymentTest {

    @Test
    void builder_setsAllFields() {
        Order order = Order.builder()
                .id("order-1")
                .orderTime(1708560000L)
                .author("Author")
                .products(java.util.List.of(new Product()))
                .build();

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("key", "value");

        Payment payment = Payment.builder()
                .id("payment-1")
                .method("VOUCHER")
                .status("WAITING_PAYMENT")
                .paymentData(paymentData)
                .order(order)
                .build();

        assertEquals("payment-1", payment.getId());
        assertEquals("VOUCHER", payment.getMethod());
        assertEquals("WAITING_PAYMENT", payment.getStatus());
        assertSame(paymentData, payment.getPaymentData());
        assertSame(order, payment.getOrder());
    }
}

