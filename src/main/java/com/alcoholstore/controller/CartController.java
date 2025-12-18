package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        // Получаем корзину из сессии
        Object cartObj = session.getAttribute("cart");

        if (cartObj == null) {
            // Если корзины нет, создаем пустую
            model.addAttribute("cart", new HashMap<Product, Integer>());
            model.addAttribute("total", BigDecimal.ZERO);
        } else {
            // Если есть, преобразуем в Map
            Map<Product, Integer> cart = (Map<Product, Integer>) cartObj;
            BigDecimal total = calculateTotal(cart);
            model.addAttribute("cart", cart);
            model.addAttribute("total", total);
        }
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        // Получаем продукт из базы данных
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));

        // Получаем или создаем корзину в сессии
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        // Добавляем или обновляем количество товара
        int currentQuantity = cart.getOrDefault(product, 0);
        cart.put(product, currentQuantity + quantity);

        redirectAttributes.addFlashAttribute("message", "Товар добавлен в корзину!");
        return "redirect:/products";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 HttpSession session) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));

        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(product);
            if (cart.isEmpty()) {
                session.removeAttribute("cart");
            }
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
    }

    private BigDecimal calculateTotal(Map<Product, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            BigDecimal productTotal = entry.getKey().getPrice()
                    .multiply(BigDecimal.valueOf(entry.getValue()));
            total = total.add(productTotal);
        }
        return total;
    }
}