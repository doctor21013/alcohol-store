package com.alcoholstore.controller;

import com.alcoholstore.model.User;
import com.alcoholstore.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Главная страница обрабатывается HomeController
    // УДАЛЕНО: метод home() с @GetMapping("/")

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    // Остальные методы остаются без изменений
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        var userOpt = userRepository.findByUsernameAndPassword(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("isAdmin", user.getIsAdmin() != null ? user.getIsAdmin() : false);
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Неверное имя пользователя или пароль");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam String confirmPassword,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        if (userRepository.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Имя пользователя уже занято");
            return "redirect:/register";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email уже используется");
            return "redirect:/register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Пароли не совпадают");
            return "redirect:/register";
        }

        user.setIsAdmin(false);
        user.setFullName("");
        user.setPhone("");

        User savedUser = userRepository.save(user);

        session.setAttribute("user", savedUser);
        session.setAttribute("username", savedUser.getUsername());
        session.setAttribute("isAdmin", savedUser.getIsAdmin() != null ? savedUser.getIsAdmin() : false);

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Получаем пользователя из сессии
        User user = (User) session.getAttribute("user");

        model.addAttribute("username", user.getUsername());
        model.addAttribute("isAdmin", user.getIsAdmin() != null ? user.getIsAdmin() : false);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostConstruct
    public void initTestUsers() {
        // Тестовый администратор
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword("admin123");
            admin.setIsAdmin(true);
            admin.setFullName("Администратор");
            userRepository.save(admin);
            System.out.println("✅ Создан тестовый администратор: admin / admin123");
        }

        // Тестовый пользователь
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword("user123");
            user.setIsAdmin(false);
            user.setFullName("Пользователь");
            userRepository.save(user);
            System.out.println("✅ Создан тестовый пользователь: user / user123");
        }
    }
}