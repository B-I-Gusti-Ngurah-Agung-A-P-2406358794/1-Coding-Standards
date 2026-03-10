package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String METHOD_VOUCHER = "VOUCHER";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        String status = determineInitialStatus(method, paymentData);

        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .method(method)
                .status(status)
                .paymentData(paymentData)
                .order(order)
                .build();

        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);

        if (STATUS_SUCCESS.equals(status)) {
            Order updatedOrder = orderService.updateStatus(
                    payment.getOrder().getId(), OrderStatus.SUCCESS.getValue());
            payment.setOrder(updatedOrder);
        } else if (STATUS_REJECTED.equals(status)) {
            Order updatedOrder = orderService.updateStatus(
                    payment.getOrder().getId(), OrderStatus.FAILED.getValue());
            payment.setOrder(updatedOrder);
        }

        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private String determineInitialStatus(String method, Map<String, String> paymentData) {
        if (METHOD_VOUCHER.equals(method)) {
            String voucherCode = paymentData.get("voucherCode");
            if (isValidVoucherCode(voucherCode)) {
                return STATUS_SUCCESS;
            }
            return STATUS_REJECTED;
        }

        if (METHOD_BANK_TRANSFER.equals(method)) {
            String bankName = paymentData.get("bankName");
            String referenceCode = paymentData.get("referenceCode");
            if (isNullOrEmpty(bankName) || isNullOrEmpty(referenceCode)) {
                return STATUS_REJECTED;
            }
            return STATUS_SUCCESS;
        }

        return "WAITING_PAYMENT";
    }

    private boolean isValidVoucherCode(String voucherCode) {
        if (voucherCode == null || voucherCode.length() != 16) {
            return false;
        }
        if (!voucherCode.startsWith("ESHOP")) {
            return false;
        }

        int digitCount = 0;
        for (char c : voucherCode.toCharArray()) {
            if (Character.isDigit(c)) {
                digitCount++;
            }
        }
        return digitCount >= 8;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}

