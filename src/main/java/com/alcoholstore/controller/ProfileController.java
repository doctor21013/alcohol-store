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
        // Получаем пользователя из сессии
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Получаем свежие данные из базы
        User user = userService.getUserById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("user", user);
        return "profile";
    }

    // Форма редактирования профиля
    @GetMapping("/edit")
    public String editProfileForm(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

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
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.getUserById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Создаем обновленного пользователя
            User updatedUser = new User();
            updatedUser.setUsername(user.getUsername()); // username не меняем
            updatedUser.setEmail(email);
            updatedUser.setFullName(fullName);
            updatedUser.setPhone(phone);
            updatedUser.setPassword(user.getPassword()); // пароль не меняем

            // Сохраняем изменения
            userService.updateUser(user.getId(), updatedUser);

            // Обновляем данные в сессии
            User freshUser = userService.getUserById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            session.setAttribute("user", freshUser);
            session.setAttribute("username", freshUser.getUsername());

            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // Форма смены пароля
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
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
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.getUserById(sessionUser.getId())
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
            User updatedUser = new User();
            updatedUser.setUsername(user.getUsername());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(newPassword);
            updatedUser.setFullName(user.getFullName());
            updatedUser.setPhone(user.getPhone());

            userService.updateUser(user.getId(), updatedUser);

            redirectAttributes.addFlashAttribute("success", "Пароль успешно изменен!");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
            return "change-password";
        }
    }
}