//package com.alcoholstore.controller;
//
//import com.alcoholstore.model.User;
//import com.alcoholstore.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//
//@Controller
//public class DebugController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @GetMapping("/debug/auth")
//    public String debugAuth(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        model.addAttribute("isAuthenticated", auth.isAuthenticated());
//        model.addAttribute("username", auth.getName());
//        model.addAttribute("authorities", auth.getAuthorities());
//        model.addAttribute("principal", auth.getPrincipal());
//
//        return "debug/auth";
//    }
//
//    @GetMapping("/debug/users")
//    public String debugUsers(Model model) {
//        List<User> users = userRepository.findAll();
//        model.addAttribute("users", users);
//        return "debug/users";
//    }
//}