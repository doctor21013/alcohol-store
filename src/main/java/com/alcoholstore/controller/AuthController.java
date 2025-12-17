package com.alcoholstore.controller;

import com.alcoholstore.model.User;
import com.alcoholstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ========== ПОКАЗ ФОРМЫ ВХОДА ==========
    @GetMapping("/login")
    public String showLoginPage(Model model,
                                @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Неверный email или пароль");
        }
        return "login";
    }

    // ========== ОБРАБОТКА ВХОДА ==========
    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        // Ищем пользователя по email
        var userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден");
            return "login";
        }

        User user = userOpt.get();

        // Проверяем пароль (простое сравнение строк)
        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Неверный пароль");
            return "login";
        }

        // Проверяем активность
        if (!user.getEnabled()) {
            model.addAttribute("error", "Аккаунт заблокирован");
            return "login";
        }

        // Сохраняем в сессию
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userRole", user.getRole());

        return "redirect:/";
    }

    // ========== ПОКАЗ ФОРМЫ РЕГИСТРАЦИИ ==========
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        return "register";
    }

    // ========== ОБРАБОТКА РЕГИСТРАЦИИ ==========
    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String phone,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        try {
            // Проверяем, существует ли пользователь
            if (userService.existsByEmail(email)) {
                model.addAttribute("error", "Пользователь с таким email уже существует");
                return "register";
            }

            // Создаем пользователя
            userService.createUser(fullName, email, password, phone);

            redirectAttributes.addFlashAttribute("success",
                    "Регистрация прошла успешно! Теперь вы можете войти в систему.");

            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            return "register";
        }
    }

    // ========== ВЫХОД ==========
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}