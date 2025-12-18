package com.alcoholstore.controller;

import com.alcoholstore.model.User;
import com.alcoholstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String account(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Проверяем, что пользователь не анонимный
        if (username.equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("isAdmin", user.isAdmin());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("phone", user.getPhone());

        return "account";
    }
}