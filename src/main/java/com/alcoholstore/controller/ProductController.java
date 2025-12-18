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

        // Проверяем авторизацию
        Object userObj = session.getAttribute("user");
        if (userObj != null) {
            product.setFavorite(false);
        }

        model.addAttribute("product", product);
        model.addAttribute("loggedIn", userObj != null);

        if (userObj != null) {
            model.addAttribute("userName", session.getAttribute("username"));
        }

        // Получаем отзывы и рейтинг для товара
        try {
            Double averageRating = reviewService.getProductAverageRating(id);
            Long reviewCount = reviewService.getProductReviewCount(id);
            var reviews = reviewService.getProductReviews(id);

            model.addAttribute("averageRating", averageRating);
            model.addAttribute("reviewCount", reviewCount);
            model.addAttribute("reviews", reviews);
        } catch (Exception e) {
            model.addAttribute("averageRating", 0.0);
            model.addAttribute("reviewCount", 0);
            model.addAttribute("reviews", List.of());
        }

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "product-detail";
    }

    // ========== ПОИСК ТОВАРОВ ==========
    @GetMapping("/search")
    public String searchProducts(@RequestParam String query, Model model, HttpSession session) {
        List<Product> searchResults = catalogService.searchProducts(query);

        model.addAttribute("products", searchResults);
        model.addAttribute("searchQuery", query);
        model.addAttribute("searchResultsCount", searchResults.size());

        Object userObj = session.getAttribute("user");
        model.addAttribute("loggedIn", userObj != null);

        if (userObj != null) {
            model.addAttribute("userName", session.getAttribute("username"));
        }

        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "catalog";
    }

    // УДАЛЕНО: ФИЛЬТРАЦИЯ ПО КАТЕГОРИИ (удали весь метод filterByCategory)

    // ========== ДОБАВЛЕНИЕ В КОРЗИНУ (через POST) ==========
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        String sessionId = cartService.getOrCreateSessionId(session);

        try {
            cartService.addToCart(sessionId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Товар добавлен в корзину");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении в корзину: " + e.getMessage());
        }

        return "redirect:/product/" + productId;
    }

    // ========== БЫСТРОЕ ДОБАВЛЕНИЕ В КОРЗИНУ ИЗ КАТАЛОГА ==========
    @PostMapping("/cart/add-quick")
    public String addToCartQuick(@RequestParam Long productId,
                                 @RequestParam(defaultValue = "1") Integer quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        String sessionId = cartService.getOrCreateSessionId(session);

        try {
            cartService.addToCart(sessionId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Товар добавлен в корзину");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении в корзину: " + e.getMessage());
        }

        return "redirect:/catalog";
    }
}