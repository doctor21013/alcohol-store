package com.alcoholstore.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Collections;  // Добавьте этот импорт

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @GetMapping
    public String dashboard(Model model) {
        // Простые тестовые данные
        model.addAttribute("totalUsers", 10);
        model.addAttribute("totalOrders", 25);
        model.addAttribute("totalRevenue", 50000.0);  // ← ИЗМЕНИТЕ на число!
        model.addAttribute("ordersToday", 3);
        model.addAttribute("newUsersToday", 1);
        model.addAttribute("recentOrders", Collections.emptyList());  // ← ДОБАВЬТЕ!

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        // Пустой список пользователей
        model.addAttribute("users", new java.util.ArrayList<>());
        return "admin/users";
    }
}