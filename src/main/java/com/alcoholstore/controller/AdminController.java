package com.alcoholstore.controller;

import com.alcoholstore.model.Order;
import com.alcoholstore.model.User;
import com.alcoholstore.service.OrderService;
import com.alcoholstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // Панель управления (дашборд)
    @GetMapping
    public String dashboard(Model model) {
        // Получаем статистику
        long totalUsers = userService.getTotalUsersCount();
        List<Order> allOrders = orderService.getAllOrders();
        List<Order> recentOrders = orderService.getRecentOrders(10);

        // Рассчитываем выручку
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order order : allOrders) {
            totalRevenue = totalRevenue.add(order.getTotalAmount());
        }

        // Заказы за сегодня
        LocalDate today = LocalDate.now();
        long ordersToday = allOrders.stream()
                .filter(order -> order.getOrderDate().toLocalDate().equals(today))
                .count();

        // Новые пользователи за сегодня
        long newUsersToday = 0; // Вам нужно реализовать этот метод в UserService

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", allOrders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("ordersToday", ordersToday);
        model.addAttribute("newUsersToday", newUsersToday);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }

    // Управление заказами
    @GetMapping("/orders")
    public String manageOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    // Управление пользователями
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
}