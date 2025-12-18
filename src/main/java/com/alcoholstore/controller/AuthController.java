package com.alcoholstore.controller;

import com.alcoholstore.model.User;
import com.alcoholstore.repository.UserRepository;
import com.alcoholstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

        var userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден");
            return "login";
        }

        User user = userOpt.get();

        // ПРЯМОЕ СРАВНЕНИЕ ПАРОЛЕЙ (без хэширования)
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
        session.setAttribute("userPhone", user.getPhone());

        return "redirect:/";
    }

    // ========== ПОКАЗ ФОРМЫ РЕГИСТРАЦИИ ==========
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        return "register";
    }

    // ========== ОБРАБОТКА РЕГИСТРАЦИИ ==========
    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                               @RequestParam String email,
                               @RequestParam String phone,
                               @RequestParam String password,
                               @RequestParam(required = false) Boolean ageConfirmed,
                               RedirectAttributes redirectAttributes) {

        try {
            // Проверка на существующего пользователя
            if (userRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Пользователь с таким email уже существует");
                return "redirect:/register";
            }

            // Проверка телефона
            if (userRepository.findByPhone(phone).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Пользователь с таким телефоном уже существует");
                return "redirect:/register";
            }

            // Проверка подтверждения возраста
            if (ageConfirmed == null || !ageConfirmed) {
                redirectAttributes.addFlashAttribute("error", "Необходимо подтвердить, что вам есть 18 лет");
                return "redirect:/register";
            }

            // Создание пользователя (пароль сохраняется как есть)
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password); // Пароль без кодирования
            user.setAgeConfirmed(true);
            user.setRole("USER");
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "Регистрация успешна! Теперь войдите в систему.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка регистрации: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // ========== ВЫХОД ==========
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}