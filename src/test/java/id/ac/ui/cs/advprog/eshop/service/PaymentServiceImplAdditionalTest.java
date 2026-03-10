package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplAdditionalTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order("order-1", List.of(new Product()), 1L, "Author");
    }

    @Test
    void addPayment_unknownMethod_defaultsToWaitingPayment() {
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "UNKNOWN", Map.of());

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();

        assertNotNull(saved.getId());
        assertEquals("UNKNOWN", saved.getMethod());
        assertEquals("WAITING_PAYMENT", saved.getStatus());
        assertEquals(result, saved);
    }

    @Test
    void addPayment_voucher_rejectsWhenNullVoucherCode() {
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "VOUCHER", new HashMap<>());

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();
        assertEquals("REJECTED", saved.getStatus());
        assertEquals(result, saved);
    }

    @Test
    void addPayment_voucher_rejectsWhenPrefixIsNotEshop() {
        Map<String, String> data = new HashMap<>();
        // 16 characters, but does NOT start with "ESHOP"
        data.put("voucherCode", "ABCD1234EFGH5678");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "VOUCHER", data);

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();
        assertEquals("REJECTED", saved.getStatus());
        assertEquals(result, saved);
    }

    @Test
    void addPayment_voucher_rejectsWhenDigitCountLessThanEight() {
        Map<String, String> data = new HashMap<>();
        // 16 chars, starts with ESHOP, but only 3 digits: 1,2,3
        data.put("voucherCode", "ESHOPABCDEF123GH");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "VOUCHER", data);

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();
        assertEquals("REJECTED", saved.getStatus());
        assertEquals(result, saved);
    }

    @Test
    void addPayment_bankTransfer_rejectsWhenBankNameNull() {
        Map<String, String> data = new HashMap<>();
        data.put("bankName", null);
        data.put("referenceCode", "REF");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "BANK_TRANSFER", data);

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();
        assertEquals("REJECTED", saved.getStatus());
        assertEquals(result, saved);
    }

    @Test
    void setStatus_otherStatus_updatesOnlyPaymentAndSaves() {
        Payment payment = Payment.builder()
                .id("p1")
                .method("VOUCHER")
                .status("WAITING_PAYMENT")
                .paymentData(Map.of("voucherCode", "ESHOP1234ABC5678"))
                .order(order)
                .build();

        paymentService.setStatus(payment, "PENDING");

        assertEquals("PENDING", payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(orderService, times(0)).updateStatus(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }
}

