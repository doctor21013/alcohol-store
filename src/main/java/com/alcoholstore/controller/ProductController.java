package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.CatalogService;
import com.alcoholstore.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ReviewService reviewService;

    // ========== СТРАНИЦА ДЕТАЛЕЙ ТОВАРА ==========
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Product> productOpt = catalogService.getProductById(id);

        if (productOpt.isEmpty()) {
            return "redirect:/catalog";
        }

        Product product = productOpt.get();
        model.addAttribute("product", product);

        // Количество товаров в корзине
        model.addAttribute("cartItemsCount", cartService.getCartItemCount(session));

        return "product-detail";
    }

    // ========== ПОИСК ТОВАРОВ ==========
    @GetMapping("/search")
    public String searchProducts(@RequestParam String query, Model model, HttpSession session) {
        List<Product> searchResults = catalogService.searchProducts(query);

        model.addAttribute("products", searchResults);
        model.addAttribute("searchQuery", query);
        model.addAttribute("searchResultsCount", searchResults.size());

        // Количество товаров в корзине
        model.addAttribute("cartItemsCount", cartService.getCartItemCount(session));

        return "catalog";
    }

    // ========== ДОБАВЛЕНИЕ В КОРЗИНУ (через POST) ==========


    // ========== БЫСТРОЕ ДОБАВЛЕНИЕ В КОРЗИНУ ИЗ КАТАЛОГА ==========
    @PostMapping("/cart/add-quick")
    public String addToCartQuick(@RequestParam Long productId,
                                 @RequestParam(defaultValue = "1") Integer quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        try {
            cartService.addToCart(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Товар добавлен в корзину");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении в корзину: " + e.getMessage());
        }

        return "redirect:/catalog";
    }
}