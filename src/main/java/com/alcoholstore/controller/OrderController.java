package com.alcoholstore.controller;

import com.alcoholstore.model.Order;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    // ========== МОИ ЗАКАЗЫ ==========
    @GetMapping
    public String myOrders(HttpSession session, Model model) {
        // Проверяем авторизацию через сессию
        if (session.getAttribute("userEmail") == null) {
            return "redirect:/login";
        }

        String userEmail = (String) session.getAttribute("userEmail");
        List<Order> orders = orderService.getOrdersByEmail(userEmail);

        model.addAttribute("orders", orders);
        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userEmail", userEmail);

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "orders";
    }

    // ========== ОФОРМЛЕНИЕ ЗАКАЗА ==========
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        // Проверяем авторизацию
        if (session.getAttribute("userEmail") == null) {
            return "redirect:/login";
        }

        String sessionId = cartService.getOrCreateSessionId(session);
        var cartItems = cartService.getCartItems(sessionId);
        BigDecimal totalPrice = cartService.getTotalPrice(sessionId);

        // Если корзина пуста
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userEmail", session.getAttribute("userEmail"));
        model.addAttribute("userPhone", session.getAttribute("userPhone")); // Добавляем телефон из сессии
        model.addAttribute("loggedIn", true);

        return "checkout";
    }

    // ========== СОЗДАНИЕ ЗАКАЗА ==========
    @PostMapping("/create")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String customerEmail,
                             @RequestParam String customerPhone,
                             @RequestParam String deliveryAddress,
                             @RequestParam(required = false) String notes,
                             HttpSession session,
                             Model model) {

        // Проверяем авторизацию
        if (session.getAttribute("userEmail") == null) {
            return "redirect:/login";
        }

        String sessionId = cartService.getOrCreateSessionId(session);
        BigDecimal totalAmount = cartService.getTotalPrice(sessionId);

        // Проверяем, что корзина не пуста
        var cartItems = cartService.getCartItems(sessionId);
        if (cartItems.isEmpty()) {
            model.addAttribute("error", "Корзина пуста");
            return "redirect:/cart";
        }

        try {
            // Создаем заказ
            Order order = orderService.createOrderFromCart(
                    sessionId,
                    customerName,
                    customerEmail,
                    customerPhone,
                    deliveryAddress,
                    notes
            );

            // Очищаем корзину
            cartService.clearCart(sessionId);

            // Сохраняем номер заказа для страницы успеха
            session.setAttribute("lastOrderNumber", order.getId());
            session.setAttribute("lastOrderTotal", totalAmount);

            return "redirect:/orders/success";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при оформлении заказа: " + e.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    // ========== СТРАНИЦА УСПЕШНОГО ОФОРМЛЕНИЯ ==========
    @GetMapping("/success")
    public String orderSuccess(HttpSession session, Model model) {
        Long orderNumber = (Long) session.getAttribute("lastOrderNumber");
        BigDecimal orderTotal = (BigDecimal) session.getAttribute("lastOrderTotal");

        if (orderNumber == null) {
            return "redirect:/";
        }

        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("orderTotal", orderTotal);
        model.addAttribute("loggedIn", true);

        // Очищаем сессию от временных данных
        session.removeAttribute("lastOrderNumber");
        session.removeAttribute("lastOrderTotal");

        return "order-success";
    }
}