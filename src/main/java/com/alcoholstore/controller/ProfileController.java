package com.alcoholstore.controller;

import com.alcoholstore.model.User;
import com.alcoholstore.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Просмотр профиля
    @GetMapping
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Проверяем, что пользователь не анонимный
        if (username.equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    // Форма редактирования профиля
    @GetMapping("/edit")
    public String editProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (username.equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }

        model.addAttribute("user", user);
        return "profile-edit";
    }

    // Сохранение изменений профиля (только email)
    @PostMapping("/update")
    public String updateProfile(@RequestParam String email,
                                RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            User user = userRepository.findByUsername(username);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
                return "redirect:/login";
            }

            // Проверяем, не занят ли email другим пользователем
            Optional<User> existingUserWithEmail = userRepository.findByEmail(email);
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Этот email уже используется другим пользователем");
                return "redirect:/profile/edit";
            }

            // Обновляем email
            user.setEmail(email);

            // Сохраняем изменения
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // Форма смены пароля
    @GetMapping("/change-password")
    public String changePasswordForm() {
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
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            User user = userRepository.findByUsername(username);

            if (user == null) {
                model.addAttribute("error", "Пользователь не найден");
                return "change-password";
            }

            // Проверяем текущий пароль
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                model.addAttribute("error", "Текущий пароль неверен");
                return "change-password";
            }

            // Проверяем, что новый пароль и подтверждение совпадают
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Новый пароль и подтверждение не совпадают");
                return "change-password";
            }

            // Обновляем пароль
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "Пароль успешно изменен!");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
            return "change-password";
        }
    }
}