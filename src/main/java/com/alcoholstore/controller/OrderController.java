package com.alcoholstore.controller;

import com.alcoholstore.model.Cart;
import com.alcoholstore.model.Order;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // Проверяем авторизацию через Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }

        String username = auth.getName();
        List<Order> orders = orderService.getOrdersByUsername(username);

        model.addAttribute("orders", orders);
        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", username);

        // Количество товаров в корзине через новый CartService
        model.addAttribute("cartItemsCount", cartService.getCartItemCount(session));

        return "orders";
    }

    // ========== ОФОРМЛЕНИЕ ЗАКАЗА ==========
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        // Проверяем авторизацию
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }

        // Получаем корзину из нового CartService
        Cart cart = cartService.getCurrentCart(session);

        // Если корзина пуста
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("totalPrice", BigDecimal.valueOf(cart.getTotalPrice()));
        model.addAttribute("userName", auth.getName());
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }

        // Получаем корзину
        Cart cart = cartService.getCurrentCart(session);
        if (cart == null || cart.getItems().isEmpty()) {
            model.addAttribute("error", "Корзина пуста");
            return "redirect:/cart";
        }

        try {
            // Создаем заказ через новый метод OrderService
            Order order = orderService.createOrderFromCart(
                    cart,
                    customerName,
                    customerEmail,
                    customerPhone,
                    deliveryAddress,
                    notes
            );

            // Очищаем корзину
            cartService.clearCart(session);

            // Сохраняем номер заказа для страницы успеха
            session.setAttribute("lastOrderNumber", order.getId());
            session.setAttribute("lastOrderTotal", order.getTotalAmount());

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