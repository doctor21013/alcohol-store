package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class CatalogController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/catalog")
    public String catalog(@RequestParam(value = "category", required = false) String category,
                          @RequestParam(value = "search", required = false) String search,
                          Model model) {

        List<Product> products;
        List<String> categories = productRepository.findAllCategories();

        if (search != null && !search.trim().isEmpty()) {
            // Поиск по названию
            products = productRepository.findByNameContainingIgnoreCase(search);
            model.addAttribute("searchQuery", search);
        } else if (category != null && !category.isEmpty()) {
            // Фильтрация по категории
            products = productRepository.findByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            // Все товары
            products = productRepository.findByIsAvailableTrue();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "catalog";
    }
}