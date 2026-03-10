package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;
    private List<Payment> payments;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        products.add(product);

        Order order = new Order("order-1", products, 1708560000L, "Author");

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("key", "value");

        payments = new ArrayList<>();
        payments.add(Payment.builder()
                .id("payment-1")
                .method("VOUCHER")
                .status("SUCCESS")
                .paymentData(paymentData)
                .order(order)
                .build());
        payments.add(Payment.builder()
                .id("payment-2")
                .method("BANK_TRANSFER")
                .status("REJECTED")
                .paymentData(paymentData)
                .order(order)
                .build());
    }

    @Test
    void save_createsOrUpdatesPayment() {
        Payment payment = payments.get(0);

        Payment saved = paymentRepository.save(payment);
        assertSame(payment, saved);

        Payment found = paymentRepository.findById("payment-1");
        assertEquals("payment-1", found.getId());
        assertEquals("VOUCHER", found.getMethod());
        assertEquals("SUCCESS", found.getStatus());
    }

    @Test
    void save_updatesExistingPayment() {
        Payment original = payments.get(0);
        paymentRepository.save(original);

        Payment updated = Payment.builder()
                .id(original.getId())
                .method(original.getMethod())
                .status("REJECTED")
                .paymentData(original.getPaymentData())
                .order(original.getOrder())
                .build();

        paymentRepository.save(updated);

        Payment found = paymentRepository.findById(original.getId());
        assertEquals("REJECTED", found.getStatus());
    }

    @Test
    void findById_returnsNullIfNotFound() {
        assertNull(paymentRepository.findById("does-not-exist"));
    }

    @Test
    void findAll_returnsAllSavedPayments() {
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }

        List<Payment> all = paymentRepository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(p -> p.getId().equals("payment-1")));
        assertTrue(all.stream().anyMatch(p -> p.getId().equals("payment-2")));
    }
}

