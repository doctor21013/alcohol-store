//package com.alcoholstore.controller;
//
//import com.alcoholstore.model.User;
//import com.alcoholstore.service.UserService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//public class LoginController {
//
//    @Autowired
//    private UserService userService;
//
//    // Показ формы входа
//    @GetMapping("/login")
//    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
//                                @RequestParam(value = "logout", required = false) String logout,
//                                Model model) {
//        if (error != null) {
//            model.addAttribute("error", "Неверный email или пароль");
//        }
//        if (logout != null) {
//            model.addAttribute("message", "Вы успешно вышли из системы");
//        }
//        return "login";
//    }
//
//    // Показ формы регистрации
//    @GetMapping("/register")
//    public String showRegisterPage(Model model) {
//        return "register";
//    }
//
//    // Обработка регистрации
//    @PostMapping("/register")
//    public String register(@RequestParam String fullName,
//                           @RequestParam String email,
//                           @RequestParam String password,
//                           @RequestParam String phone,
//                           Model model,
//                           RedirectAttributes redirectAttributes) {
//
//        try {
//            // Проверяем, существует ли пользователь
//            if (userService.existsByEmail(email)) {
//                model.addAttribute("error", "Пользователь с таким email уже существует");
//                return "register";
//            }
//
//            // Создаем пользователя
//            userService.createUser(fullName, email, password, phone);
//
//            redirectAttributes.addFlashAttribute("success",
//                    "Регистрация прошла успешно! Теперь вы можете войти в систему.");
//
//            return "redirect:/login";
//
//        } catch (Exception e) {
//            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
//            return "register";
//        }
//    }
//
//    // Установка данных в сессию после успешного входа
//    @GetMapping("/login/success")
//    public String loginSuccess(HttpSession session) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//
//        userService.getUserByEmail(email).ifPresent(user -> {
//            session.setAttribute("userId", user.getId());
//            session.setAttribute("userName", user.getFullName());
//            session.setAttribute("userEmail", user.getEmail());
//            session.setAttribute("userRole", user.getRole());
//        });
//
//        return "redirect:/";
//    }
//}