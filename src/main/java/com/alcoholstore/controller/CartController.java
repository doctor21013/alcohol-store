package com.alcoholstore.controller;

import com.alcoholstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Просмотр корзины
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        model.addAttribute("cart", cartService.getCurrentCart(session));
        model.addAttribute("cartItemCount", cartService.getCartItemCount(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        return "cart";
    }

    // Добавить товар в корзину
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Товар добавлен в корзину!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/catalog";
    }

    // Обновить количество
    @PostMapping("/update")
    public String updateCart(@RequestParam Long productId,
                             @RequestParam Integer quantity,
                             HttpSession session) {
        cartService.updateQuantity(session, productId, quantity);
        return "redirect:/cart";
    }

    // Удалить товар из корзины
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 HttpSession session) {
        cartService.removeFromCart(session, productId);
        return "redirect:/cart";
    }

    // Очистить корзину
    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart";
    }
}