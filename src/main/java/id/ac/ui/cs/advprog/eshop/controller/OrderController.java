package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentService paymentService;

    @Autowired
    public OrderController(OrderService orderService,
                           ProductService productService,
                           PaymentService paymentService) {
        this.orderService = orderService;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "orderCreate";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam("author") String author,
                              @RequestParam("productId") String productId) {
        Product product = productService.findById(productId);
        if (product == null) {
            return "redirect:/order/create";
        }

        List<Product> products = List.of(product);
        Order order = Order.builder()
                .id(java.util.UUID.randomUUID().toString())
                .products(products)
                .orderTime(System.currentTimeMillis())
                .author(author)
                .build();

        orderService.createOrder(order);
        return "redirect:/order/history";
    }

    @GetMapping("/history")
    public String historyForm(Model model) {
        model.addAttribute("author", "");
        model.addAttribute("orders", null);
        return "orderHistory";
    }

    @PostMapping("/history")
    public String historyByAuthor(@RequestParam("author") String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("author", author);
        model.addAttribute("orders", orders);
        return "orderHistory";
    }

    @GetMapping("/pay/{orderId}")
    public String payOrderPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }
        model.addAttribute("order", order);
        return "orderPay";
    }

    @PostMapping("/pay/{orderId}")
    public String payOrder(@PathVariable String orderId,
                           @RequestParam("method") String method,
                           @RequestParam Map<String, String> allParams,
                           Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }

        Map<String, String> paymentData = new HashMap<>();
        if ("VOUCHER".equals(method)) {
            paymentData.put("voucherCode", allParams.get("voucherCode"));
        } else if ("BANK_TRANSFER".equals(method)) {
            paymentData.put("bankName", allParams.get("bankName"));
            paymentData.put("referenceCode", allParams.get("referenceCode"));
        }

        Payment payment = paymentService.addPayment(order, method, paymentData);
        model.addAttribute("payment", payment);
        return "orderPayResult";
    }
}

