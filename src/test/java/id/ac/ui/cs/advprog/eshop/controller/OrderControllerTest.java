package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void getCreatePage_returnsOrderCreateView_withProductsModel() throws Exception {
        when(productService.findAll()).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderCreate"))
                .andExpect(model().attributeExists("products"));

        verify(productService, times(1)).findAll();
    }

    @Test
    void postCreate_redirectsToCreateWhenProductNotFound() throws Exception {
        when(productService.findById("missing")).thenReturn(null);

        mockMvc.perform(post("/order/create")
                        .param("author", "A")
                        .param("productId", "missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/create"));

        verify(orderService, times(0)).createOrder(any(Order.class));
    }

    @Test
    void postCreate_redirectsToHistoryWhenProductFound() throws Exception {
        Product p = new Product();
        p.setProductId("p1");
        p.setProductName("P");
        p.setProductQuantity(1);
        when(productService.findById("p1")).thenReturn(p);

        mockMvc.perform(post("/order/create")
                        .param("author", "A")
                        .param("productId", "p1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/history"));

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void getHistoryForm_returnsOrderHistoryView() throws Exception {
        mockMvc.perform(get("/order/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"))
                .andExpect(model().attributeExists("author"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void postHistory_returnsOrderHistoryView_withOrders() throws Exception {
        when(orderService.findAllByAuthor("A")).thenReturn(List.of());

        mockMvc.perform(post("/order/history").param("author", "A"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"))
                .andExpect(model().attributeExists("orders"));

        verify(orderService, times(1)).findAllByAuthor("A");
    }

    @Test
    void getPayPage_redirectsWhenOrderMissing_orShowsViewWhenFound() throws Exception {
        when(orderService.findById("missing")).thenReturn(null);

        mockMvc.perform(get("/order/pay/missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/history"));

        Order order = Order.builder()
                .id("o1")
                .products(List.of(new Product()))
                .orderTime(1L)
                .author("A")
                .status(id.ac.ui.cs.advprog.eshop.enums.OrderStatus.WAITING_PAYMENT.getValue())
                .build();
        when(orderService.findById("o1")).thenReturn(order);

        mockMvc.perform(get("/order/pay/o1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderPay"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void postPay_redirectsWhenOrderMissing() throws Exception {
        when(orderService.findById("missing")).thenReturn(null);

        mockMvc.perform(post("/order/pay/missing")
                        .param("method", "VOUCHER")
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/history"));

        verify(paymentService, times(0)).addPayment(any(Order.class), any(String.class), anyMap());
    }

    @Test
    void postPay_voucher_buildsVoucherPaymentData() throws Exception {
        Order order = Order.builder()
                .id("o1")
                .products(List.of(new Product()))
                .orderTime(1L)
                .author("A")
                .status(id.ac.ui.cs.advprog.eshop.enums.OrderStatus.WAITING_PAYMENT.getValue())
                .build();
        when(orderService.findById("o1")).thenReturn(order);

        Payment payment = Payment.builder().id("pay-1").status("SUCCESS").method("VOUCHER").order(order).paymentData(Map.of("voucherCode", "ESHOP1234ABC5678")).build();
        when(paymentService.addPayment(eq(order), eq("VOUCHER"), eq(Map.of("voucherCode", "ESHOP1234ABC5678"))))
                .thenReturn(payment);

        mockMvc.perform(post("/order/pay/o1")
                        .param("method", "VOUCHER")
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderPayResult"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void postPay_bankTransfer_buildsBankTransferPaymentData() throws Exception {
        Order order = Order.builder()
                .id("o1")
                .products(List.of(new Product()))
                .orderTime(1L)
                .author("A")
                .status(id.ac.ui.cs.advprog.eshop.enums.OrderStatus.WAITING_PAYMENT.getValue())
                .build();
        when(orderService.findById("o1")).thenReturn(order);

        Map<String, String> data = Map.of("bankName", "BCA", "referenceCode", "REF");
        Payment payment = Payment.builder().id("pay-2").status("SUCCESS").method("BANK_TRANSFER").order(order).paymentData(data).build();
        when(paymentService.addPayment(eq(order), eq("BANK_TRANSFER"), eq(data))).thenReturn(payment);

        mockMvc.perform(post("/order/pay/o1")
                        .param("method", "BANK_TRANSFER")
                        .param("bankName", "BCA")
                        .param("referenceCode", "REF"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderPayResult"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void postPay_unknownMethod_sendsEmptyPaymentData() throws Exception {
        Order order = Order.builder()
                .id("o1")
                .products(List.of(new Product()))
                .orderTime(1L)
                .author("A")
                .status(id.ac.ui.cs.advprog.eshop.enums.OrderStatus.WAITING_PAYMENT.getValue())
                .build();
        when(orderService.findById("o1")).thenReturn(order);

        Payment payment = Payment.builder().id("pay-3").status("WAITING_PAYMENT").method("UNKNOWN").order(order).paymentData(Map.of()).build();
        when(paymentService.addPayment(eq(order), eq("UNKNOWN"), eq(Map.of()))).thenReturn(payment);

        mockMvc.perform(post("/order/pay/o1")
                        .param("method", "UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderPayResult"))
                .andExpect(model().attributeExists("payment"));
    }
}

