package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.model.User;
import com.alcoholstore.service.ProductService;
import com.alcoholstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminDashboard(Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        return "admin/admin";
    }

    @GetMapping("/products")
    public String adminProducts(Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/products"; // Админская страница товаров
    }

    @GetMapping("/products/new")
    public String showAddProductForm(Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Товар сохранен");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        Product product = productService.getProductById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Товар не найден");
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        return "admin/product-form";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Товар удален");
        return "redirect:/admin/products";
    }

    @GetMapping("/users")
    public String adminUsers(Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/toggle-role/{id}")
    public String toggleUserRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        User user = userService.getUserByIdOrThrow(id);
        if ("ROLE_ADMIN".equals(user.getRole())) {
            user.setRole("ROLE_USER");
        } else {
            user.setRole("ROLE_ADMIN");
        }
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Роль пользователя изменена");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен");
            return "redirect:/";
        }
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "Пользователь удален");
        return "redirect:/admin/users";
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return user != null && "ROLE_ADMIN".equals(user.getRole());
    }
}