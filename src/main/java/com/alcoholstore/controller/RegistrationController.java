//package com.alcoholstore.controller;
//
//import com.alcoholstore.model.User;
//import com.alcoholstore.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//public class RegistrationController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @GetMapping("/register")
//    public String showRegistrationForm(Model model) {
//        return "register";
//    }
//
//    @PostMapping("/register")
//    public String registerUser(@RequestParam String username,
//                               @RequestParam String password,
//                               @RequestParam String email,
//                               Model model) {
//        // Проверка, существует ли пользователь
//        if (userRepository.findByUsername(username) != null) {
//            model.addAttribute("error", "Пользователь с таким именем уже существует");
//            return "register";
//        }
//
//        // Проверка email
//        if (userRepository.findByEmail(email) != null) {
//            model.addAttribute("error", "Пользователь с таким email уже существует");
//            return "register";
//        }
//
//        // Создание нового пользователя
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setEmail(email);
//        user.setRole("ROLE_USER");
//        user.setEnabled(true);
//
//        // Сохранение пользователя
//        userRepository.save(user);
//
//        return "redirect:/login?registered";
//    }
//}