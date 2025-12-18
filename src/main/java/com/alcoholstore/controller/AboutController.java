package com.alcoholstore.controller;

import com.alcoholstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @Autowired
    private CartService cartService;

    @GetMapping("/about")
    public String about(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Проверяем авторизацию через Spring Security
        if (userDetails != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("userName", userDetails.getUsername());

            // Количество товаров в корзине через Spring Security
            int cartItemsCount = cartService.getCartItemCount(userDetails.getUsername());
            model.addAttribute("cartItemsCount", cartItemsCount);
        } else {
            model.addAttribute("loggedIn", false);
            model.addAttribute("cartItemsCount", 0);
        }

        return "about";
    }
}