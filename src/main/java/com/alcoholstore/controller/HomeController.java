package com.alcoholstore.controller;

import com.alcoholstore.service.CartService;
import com.alcoholstore.service.CatalogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // Проверяем, авторизован ли пользователь
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("userRole", session.getAttribute("userRole"));
        } else {
            model.addAttribute("loggedIn", false);
        }

        // Получаем популярные товары (первые 6)
        var products = catalogService.getAllProducts();
        int limit = Math.min(6, products.size());
        model.addAttribute("popularProducts", products.subList(0, limit));

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "index";
    }
}