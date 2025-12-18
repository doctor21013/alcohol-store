package com.alcoholstore.controller;

import com.alcoholstore.model.Cart;
import com.alcoholstore.model.Order;
import com.alcoholstore.model.User;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.OrderService;
import com.alcoholstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/my-orders")  // ← ИЗМЕНИЛ НА /my-orders чтобы не конфликтовать с админкой
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    // ========== МОИ ЗАКАЗЫ (для обычных пользователей) ==========
    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        List<Order> orders = orderService.getOrdersByUsername(username);
        User user = userService.findByUsername(username);

        model.addAttribute("orders", orders);
        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", username);
        model.addAttribute("user", user);

        // Попробуем получить количество товаров в корзине
        try {
            int cartCount = cartService.getCartItemCount(username);
            model.addAttribute("cartItemsCount", cartCount);
        } catch (Exception e) {
            model.addAttribute("cartItemsCount", 0);
        }

        return "orders"; // Это шаблон для пользователей
    }

    // ========== ОФОРМЛЕНИЕ ЗАКАЗА ==========
    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        Cart cart = cartService.getCart(username);

        // Если корзина пуста
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        // Получаем данные пользователя
        User user = userService.findByUsername(username);

        // Заполняем данные пользователя по умолчанию
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        model.addAttribute("userName", username);
        model.addAttribute("loggedIn", true);
        model.addAttribute("user", user);

        // Предзаполняем данные пользователя
        model.addAttribute("defaultName", user != null ? user.getUsername() : "");
        model.addAttribute("defaultEmail", user != null && user.getEmail() != null ? user.getEmail() : "");

        return "checkout";
    }

    // ========== СОЗДАНИЕ ЗАКАЗА ==========
    @PostMapping("/create")
    public String placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String customerName,
            @RequestParam String customerEmail,
            @RequestParam String customerPhone,
            @RequestParam String deliveryAddress,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();

        try {
            // Создаем заказ через OrderService
            Order order = orderService.createOrderFromCart(
                    username,
                    customerName,
                    customerEmail,
                    customerPhone,
                    deliveryAddress,
                    notes
            );

            // Сохраняем номер заказа для страницы успеха
            redirectAttributes.addFlashAttribute("orderNumber", order.getId());
            redirectAttributes.addFlashAttribute("orderTotal", order.getTotalAmount());
            redirectAttributes.addFlashAttribute("success", "Заказ успешно оформлен!");

            return "redirect:/my-orders/success";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ошибка при оформлении заказа: " + e.getMessage());
            return "redirect:/my-orders/checkout";
        }
    }

    // ========== СТРАНИЦА УСПЕШНОГО ОФОРМЛЕНИЯ ==========
    @GetMapping("/success")
    public String orderSuccess(@AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Данные заказа должны приходить из redirectAttributes
        if (!model.containsAttribute("orderNumber")) {
            return "redirect:/";
        }

        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", userDetails.getUsername());

        try {
            model.addAttribute("cartItemsCount", cartService.getCartItemCount(userDetails.getUsername()));
        } catch (Exception e) {
            model.addAttribute("cartItemsCount", 0);
        }

        return "order-success";
    }

    // ========== ДЕТАЛИ ЗАКАЗА ==========
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        Order order = orderService.getOrderById(id).orElse(null);
        User user = userService.findByUsername(username);

        // Проверяем, что заказ принадлежит пользователю
        if (order == null || order.getUser() == null ||
                !order.getUser().getUsername().equals(username)) {
            return "redirect:/my-orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", username);
        model.addAttribute("user", user);

        return "order-details";
    }
}