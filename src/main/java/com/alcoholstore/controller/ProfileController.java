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
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    // Просмотр профиля
    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("user", user);
        return "profile";
    }

    // Форма редактирования профиля
    @GetMapping("/edit")
    public String editProfileForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("user", user);
        return "profile-edit";
    }

    // Сохранение изменений профиля
    @PostMapping("/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);

        userService.updateUser(userId, user);

        // Обновляем данные в сессии
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("userEmail", user.getEmail());

        redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен!");
        return "redirect:/profile";
    }

    // Форма смены пароля
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        return "change-password";
    }

    // Смена пароля
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем текущий пароль
        if (!user.getPassword().equals(currentPassword)) {
            model.addAttribute("error", "Текущий пароль неверен");
            return "change-password";
        }

        // Проверяем, что новый пароль и подтверждение совпадают
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Новый пароль и подтверждение не совпадают");
            return "change-password";
        }

        // Обновляем пароль
        user.setPassword(newPassword);
        userService.updateUser(userId, user);

        redirectAttributes.addFlashAttribute("success", "Пароль успешно изменен!");
        return "redirect:/profile";
    }
}