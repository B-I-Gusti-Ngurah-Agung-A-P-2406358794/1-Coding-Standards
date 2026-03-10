package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        products.add(product);

        order = new Order("order-1", products, 1708560000L, "Author");
    }

    @Test
    void addPayment_createsSuccessfulVoucherPayment_whenVoucherIsValid() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "VOUCHER", paymentData);

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();

        assertNotNull(saved.getId());
        assertSame(order, saved.getOrder());
        assertEquals("VOUCHER", saved.getMethod());
        assertEquals("SUCCESS", saved.getStatus());
        assertEquals("ESHOP1234ABC5678", saved.getPaymentData().get("voucherCode"));

        assertSame(saved, result);
    }

    @Test
    void addPayment_createsRejectedVoucherPayment_whenVoucherIsInvalid() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "INVALID-CODE");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "VOUCHER", paymentData);

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();

        assertEquals("REJECTED", saved.getStatus());
        assertSame(saved, result);
    }

    @Test
    void addPayment_createsRejectedBankTransferPayment_whenMissingRequiredData() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "BANK_TRANSFER", paymentData);

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();

        assertEquals("REJECTED", saved.getStatus());
        assertSame(saved, result);
    }

    @Test
    void addPayment_createsSuccessfulBankTransferPayment_whenDataIsValid() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "REF-123456");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment result = paymentService.addPayment(order, "BANK_TRANSFER", paymentData);

        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();

        assertEquals("SUCCESS", saved.getStatus());
        assertSame(saved, result);
    }

    @Test
    void setStatus_updatesOrderStatusOnSuccess() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        Payment payment = Payment.builder()
                .id("payment-1")
                .method("VOUCHER")
                .status("WAITING_PAYMENT")
                .paymentData(paymentData)
                .order(order)
                .build();

        Order successfulOrder = new Order(order.getId(), order.getProducts(),
                order.getOrderTime(), order.getAuthor(), OrderStatus.SUCCESS.getValue());

        doReturn(successfulOrder).when(orderService)
                .updateStatus(order.getId(), OrderStatus.SUCCESS.getValue());

        Payment result = paymentService.setStatus(payment, "SUCCESS");

        verify(orderService, times(1))
                .updateStatus(order.getId(), OrderStatus.SUCCESS.getValue());
        verify(paymentRepository, times(1)).save(payment);

        assertEquals("SUCCESS", result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), result.getOrder().getStatus());
    }

    @Test
    void setStatus_updatesOrderStatusOnRejected() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "INVALID-CODE");

        Payment payment = Payment.builder()
                .id("payment-1")
                .method("VOUCHER")
                .status("WAITING_PAYMENT")
                .paymentData(paymentData)
                .order(order)
                .build();

        Order failedOrder = new Order(order.getId(), order.getProducts(),
                order.getOrderTime(), order.getAuthor(), OrderStatus.FAILED.getValue());

        doReturn(failedOrder).when(orderService)
                .updateStatus(order.getId(), OrderStatus.FAILED.getValue());

        Payment result = paymentService.setStatus(payment, "REJECTED");

        verify(orderService, times(1))
                .updateStatus(order.getId(), OrderStatus.FAILED.getValue());
        verify(paymentRepository, times(1)).save(payment);

        assertEquals("REJECTED", result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), result.getOrder().getStatus());
    }

    @Test
    void getPayment_delegatesToRepository() {
        Payment payment = Payment.builder()
                .id("payment-1")
                .method("VOUCHER")
                .status("SUCCESS")
                .paymentData(new HashMap<>())
                .order(order)
                .build();

        doReturn(payment).when(paymentRepository).findById("payment-1");

        Payment result = paymentService.getPayment("payment-1");

        assertSame(payment, result);
        verify(paymentRepository, times(1)).findById("payment-1");
    }

    @Test
    void getAllPayments_delegatesToRepository() {
        List<Payment> payments = new ArrayList<>();
        payments.add(Payment.builder()
                .id("payment-1")
                .method("VOUCHER")
                .status("SUCCESS")
                .paymentData(new HashMap<>())
                .order(order)
                .build());

        doReturn(payments).when(paymentRepository).findAll();

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(1, result.size());
        assertSame(payments, result);
        verify(paymentRepository, times(1)).findAll();
    }
}

