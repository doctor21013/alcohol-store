package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        // Получаем товары для главной страницы
        List<Product> products = productService.getAllProducts();

        // Ограничиваем количество для отображения
        if (products != null && products.size() > 4) {
            products = products.subList(0, 4);
        }

        model.addAttribute("products", products);
        return "index";
    }
}