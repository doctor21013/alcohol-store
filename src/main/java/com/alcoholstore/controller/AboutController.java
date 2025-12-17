package com.alcoholstore.controller;

import com.alcoholstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @Autowired
    private CartService cartService;

    @GetMapping("/about")
    public String about(HttpSession session, Model model) {
        // Проверяем авторизацию
        if (session.getAttribute("userEmail") != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("userName", session.getAttribute("userName"));
        } else {
            model.addAttribute("loggedIn", false);
        }

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        int cartItemsCount = cartService.getCartItemsCount(sessionId);
        model.addAttribute("cartItemsCount", cartItemsCount);

        return "about";
    }
}