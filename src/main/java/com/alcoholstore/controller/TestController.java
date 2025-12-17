package com.alcoholstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test-simple")
    public String testSimple() {
        System.out.println("🚀 TestController.testSimple() вызван!");
        return "test"; // Убедитесь, что test.html в templates/
    }
}