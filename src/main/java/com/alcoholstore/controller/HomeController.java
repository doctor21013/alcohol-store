package com.alcoholstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Создаем список популярных товаров
        List<Product> popularProducts = Arrays.asList(
                new Product("Виски Jack Daniel's", 2499.99,
                        "https://images.unsplash.com/photo-1516470930795-6ba256c9dab5",
                        "Виски", "Классический американский виски с мягким вкусом"),
                new Product("Водка Белуга", 1899.99,
                        "https://images.unsplash.com/photo-1621923806638-fd6a8d2a981f",
                        "Водка", "Премиальная водка с чистейшим вкусом"),
                new Product("Ром Bacardi", 1299.99,
                        "https://images.unsplash.com/photo-1589363460771-cd3c4c2d5a64",
                        "Ром", "Классический светлый ром для коктейлей"),
                new Product("Текила Jose Cuervo", 1599.99,
                        "https://images.unsplash.com/photo-1600523858665-0e4e6d2e8f5e",
                        "Текила", "Знаменитая мексиканская текила"),
                new Product("Коньяк Hennessy", 4599.99,
                        "https://images.unsplash.com/photo-1600271768111-12d5b0c8b2d8",
                        "Коньяк", "Элитный французский коньяк"),
                new Product("Вермут Martini", 1199.99,
                        "https://images.unsplash.com/photo-1577900234664-3068e678d5c5",
                        "Вермут", "Итальянский вермут для аперитива"),
                new Product("Джин Bombay Sapphire", 1799.99,
                        "https://images.unsplash.com/photo-1572490122747-3963b5351542",
                        "Джин", "Премиальный лондонский сухой джин"),
                new Product("Ликёр Baileys", 1499.99,
                        "https://images.unsplash.com/photo-1566633808645-70280c0a28f3",
                        "Ликёр", "Сливочный ирландский ликёр")
        );

        model.addAttribute("popularProducts", popularProducts);
        return "index";
    }

    // Внутренний класс Product
    public static class Product {
        private String name;
        private double price;
        private String imageUrl;
        private String category;
        private String description;

        public Product(String name, double price, String imageUrl, String category, String description) {
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
            this.category = category;
            this.description = description;
        }

        // Геттеры
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
    }
}