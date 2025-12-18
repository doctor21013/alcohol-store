package com.alcoholstore.controller;

import com.alcoholstore.model.Cart;
import com.alcoholstore.model.Order;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    // ========== МОИ ЗАКАЗЫ ==========
    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        List<Order> orders = orderService.getOrdersByUsername(username);

        model.addAttribute("orders", orders);
        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", username);
        model.addAttribute("cartItemsCount", cartService.getCartItemCount(username));

        return "orders";
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

        // Заполняем данные пользователя по умолчанию
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        model.addAttribute("userName", username);
        model.addAttribute("loggedIn", true);

        // Предзаполняем данные пользователя
        model.addAttribute("defaultName", userDetails.getUsername());

        // TODO: Если у пользователя есть профиль, можно взять email и телефон оттуда
        // model.addAttribute("defaultEmail", user.getEmail());
        // model.addAttribute("defaultPhone", user.getPhone());

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

            return "redirect:/orders/success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при оформлении заказа: " + e.getMessage());
            return "redirect:/orders/checkout";
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
        model.addAttribute("cartItemsCount", cartService.getCartItemCount(userDetails.getUsername()));

        return "order-success";
    }
}