package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CartService cartService;

    // Страница избранного
    @GetMapping
    public String favoritesPage(HttpSession session, Model model) {
        // Проверяем авторизацию через сессию
        if (session.getAttribute("userId") == null) {
            return "redirect:/login?redirect=/favorites";
        }

        Long userId = (Long) session.getAttribute("userId");
        List<Product> favorites = favoriteService.getUserFavorites(userId);

        model.addAttribute("favorites", favorites);
        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", session.getAttribute("userName"));

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "favorites";
    }

    // Добавить в избранное (AJAX)
    @PostMapping("/add")
    @ResponseBody
    public String addToFavorites(@RequestParam Long productId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "error:not_logged_in";
        }

        try {
            favoriteService.addToFavorites(userId, productId);
            return "success";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    // Удалить из избранного
    @PostMapping("/remove/{id}")
    public String removeFromFavorites(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            favoriteService.removeFromFavorites(userId, id);
            redirectAttributes.addFlashAttribute("successMessage", "Товар удален из избранного");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }

        return "redirect:/favorites";
    }

    // Проверить статус избранного (AJAX)
    @GetMapping("/check")
    @ResponseBody
    public String checkFavoriteStatus(@RequestParam Long productId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "not_logged_in";
        }

        try {
            boolean isFavorite = favoriteService.isProductInFavorites(userId, productId);
            return isFavorite ? "favorite" : "not_favorite";
        } catch (Exception e) {
            return "error";
        }
    }
}