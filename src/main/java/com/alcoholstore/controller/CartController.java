package com.alcoholstore.controller;

import com.alcoholstore.model.CartItem;
import com.alcoholstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        // Проверяем авторизацию и добавляем информацию о пользователе
        if (session.getAttribute("user") != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("userEmail", session.getAttribute("userEmail"));
        } else {
            model.addAttribute("loggedIn", false);
        }

        // Получаем данные корзины
        String sessionId = cartService.getOrCreateSessionId(session);
        List<CartItem> cartItems = cartService.getCartItems(sessionId);
        BigDecimal totalPrice = cartService.getTotalPrice(sessionId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartItemsCount", cartItems.size());

        return "cart";
    }

    // Остальные методы остаются без изменений
    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {
        String sessionId = cartService.getOrCreateSessionId(session);
        cartService.addToCart(sessionId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
                                 HttpSession session) {
        String sessionId = cartService.getOrCreateSessionId(session);
        cartService.removeFromCart(sessionId, productId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{productId}")
    public String updateCart(@PathVariable Long productId,
                             @RequestParam Integer quantity,
                             HttpSession session) {
        String sessionId = cartService.getOrCreateSessionId(session);
        cartService.updateQuantity(sessionId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        String sessionId = cartService.getOrCreateSessionId(session);
        cartService.clearCart(sessionId);
        return "redirect:/cart";
    }
}