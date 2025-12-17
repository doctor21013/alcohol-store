package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.service.CatalogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/catalog")
    public String catalog(HttpSession session, Model model) {
        // Получаем товары
        List<Product> products = catalogService.getAllProducts();
        model.addAttribute("products", products);

        // ИСПРАВЛЕННАЯ проверка авторизации
        if (session.getAttribute("userEmail") != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("userRole", session.getAttribute("userRole"));
        } else {
            model.addAttribute("loggedIn", false);
        }

        return "catalog";
    }
}