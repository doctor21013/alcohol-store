package com.alcoholstore.controller;

import com.alcoholstore.model.User;
import com.alcoholstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Просмотр профиля
    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Проверяем, что пользователь не анонимный
        if (username.equals("anonymousUser")) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return "redirect:/login?error=user_not_found";
        }

        User user = userOptional.get();

        model.addAttribute("user", user);
        return "profile";
    }

    // Форма редактирования профиля
    @GetMapping("/edit")
    public String editProfileForm(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (username.equals("anonymousUser")) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return "redirect:/login?error=user_not_found";
        }

        User user = userOptional.get();
        model.addAttribute("user", user);
        return "profile-edit";
    }

    // Сохранение изменений профиля
    @PostMapping("/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Optional<User> userOptional = userService.findByUsername(username);

            if (userOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
                return "redirect:/login";
            }

            User user = userOptional.get();

            // Обновляем данные пользователя
            user.setFullName(fullName);
            user.setEmail(email);
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone);
            }

            // Сохраняем изменения с помощью saveUser (а не updateUser)
            userService.saveUser(user);

            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // Форма смены пароля
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (username.equals("anonymousUser")) {
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Optional<User> userOptional = userService.findByUsername(username);

            if (userOptional.isEmpty()) {
                model.addAttribute("error", "Пользователь не найден");
                return "change-password";
            }

            User user = userOptional.get();

            // Проверяем текущий пароль с помощью PasswordEncoder
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                model.addAttribute("error", "Текущий пароль неверен");
                return "change-password";
            }

            // Проверяем, что новый пароль и подтверждение совпадают
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Новый пароль и подтверждение не совпадают");
                return "change-password";
            }

            // Обновляем пароль с шифрованием
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);

            redirectAttributes.addFlashAttribute("success", "Пароль успешно изменен!");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
            return "change-password";
        }
    }
}