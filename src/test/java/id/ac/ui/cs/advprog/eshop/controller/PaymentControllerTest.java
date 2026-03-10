package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
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

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void getDetailForm_returnsPaymentDetailFormView() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetailForm"));
    }

    @Test
    void getDetailById_returnsPaymentDetailView_withPaymentModel() throws Exception {
        Order order = new Order("o1", List.of(new Product()), 1L, "A");
        Payment payment = Payment.builder()
                .id("p1")
                .method("VOUCHER")
                .status("SUCCESS")
                .paymentData(Map.of())
                .order(order)
                .build();
        when(paymentService.getPayment("p1")).thenReturn(payment);

        mockMvc.perform(get("/payment/detail/p1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetail"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService, times(1)).getPayment("p1");
    }

    @Test
    void getAdminList_returnsAdminListView_withPaymentsModel() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of());

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminList"))
                .andExpect(model().attributeExists("payments"));

        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    void getAdminDetail_returnsAdminDetailView_withPaymentModel() throws Exception {
        when(paymentService.getPayment("p1")).thenReturn(null);

        mockMvc.perform(get("/payment/admin/detail/p1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminDetail"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService, times(1)).getPayment("p1");
    }

    @Test
    void postSetStatus_redirectsToListWhenPaymentMissing() throws Exception {
        when(paymentService.getPayment("missing")).thenReturn(null);

        mockMvc.perform(post("/payment/admin/set-status/missing").param("action", "accept"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));

        verify(paymentService, times(0)).setStatus(any(Payment.class), any(String.class));
    }

    @Test
    void postSetStatus_accept_setsSuccess_andReject_setsRejected() throws Exception {
        Order order = new Order("o1", List.of(new Product()), 1L, "A");
        Payment payment = Payment.builder()
                .id("p1")
                .method("VOUCHER")
                .status("WAITING_PAYMENT")
                .paymentData(Map.of())
                .order(order)
                .build();
        when(paymentService.getPayment("p1")).thenReturn(payment);

        mockMvc.perform(post("/payment/admin/set-status/p1").param("action", "accept"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/detail/p1"));
        verify(paymentService, times(1)).setStatus(eq(payment), eq("SUCCESS"));

        // second call: reject
        when(paymentService.getPayment("p1")).thenReturn(payment);
        mockMvc.perform(post("/payment/admin/set-status/p1").param("action", "reject"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/detail/p1"));
        verify(paymentService, times(1)).setStatus(eq(payment), eq("REJECTED"));
    }
}

