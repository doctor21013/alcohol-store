package com.alcoholstore.controller;

import com.alcoholstore.model.Cart;
import com.alcoholstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Показать корзину
    @GetMapping("")
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCart(userDetails.getUsername());
        model.addAttribute("cart", cart);
        model.addAttribute("title", "Корзина");

        return "cart";
    }

    // Добавить товар в корзину
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            cartService.addToCart(userDetails.getUsername(), productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Товар добавлен в корзину!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    // Удалить товар из корзины
    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            cartService.removeFromCart(userDetails.getUsername(), productId);
            redirectAttributes.addFlashAttribute("success", "Товар удален из корзины!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    // Обновить количество
    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam Integer quantity,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            cartService.updateQuantity(userDetails.getUsername(), productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Количество обновлено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    // Очистить корзину
    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        cartService.clearCart(userDetails.getUsername());
        redirectAttributes.addFlashAttribute("success", "Корзина очищена!");

        return "redirect:/cart";
    }
}