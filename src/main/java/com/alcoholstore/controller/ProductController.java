package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.CatalogService;
import com.alcoholstore.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

        // Проверяем авторизацию для добавления в избранное
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            // Здесь можно проверить, есть ли товар в избранном у пользователя
            // Пока просто устанавливаем false
            product.setFavorite(false);
        }

        model.addAttribute("product", product);
        model.addAttribute("loggedIn", userId != null);

        if (userId != null) {
            model.addAttribute("userName", session.getAttribute("userName"));
        }

        // Получаем отзывы и рейтинг для товара (если есть сервис отзывов)
        try {
            Double averageRating = reviewService.getProductAverageRating(id);
            Long reviewCount = reviewService.getProductReviewCount(id);
            var reviews = reviewService.getProductReviews(id, false);

            model.addAttribute("averageRating", averageRating);
            model.addAttribute("reviewCount", reviewCount);
            model.addAttribute("reviews", reviews);
        } catch (Exception e) {
            // Если сервис отзывов еще не реализован
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

        // Проверяем авторизацию
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("loggedIn", userId != null);

        if (userId != null) {
            model.addAttribute("userName", session.getAttribute("userName"));
        }

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "catalog";
    }

    // ========== ФИЛЬТРАЦИЯ ПО КАТЕГОРИИ ==========
    @GetMapping("/category/{categoryId}")
    public String filterByCategory(@PathVariable Long categoryId, Model model, HttpSession session) {
        List<Product> categoryProducts = catalogService.getProductsByCategory(categoryId);

        model.addAttribute("products", categoryProducts);
        model.addAttribute("categoryProductsCount", categoryProducts.size());

        // Проверяем авторизацию
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("loggedIn", userId != null);

        if (userId != null) {
            model.addAttribute("userName", session.getAttribute("userName"));
        }

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "catalog";
    }

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

    // ========== ПРОСМОТР ИСТОРИИ ПРОСМОТРОВ ==========
    @GetMapping("/history")
    public String viewHistory(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // Здесь можно получить историю просмотров из сессии или базы данных
        // Пока просто редирект на главную
        return "redirect:/";
    }
}