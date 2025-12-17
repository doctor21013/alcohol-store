package com.alcoholstore.controller;

import com.alcoholstore.service.CartService;
import com.alcoholstore.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CartService cartService;

    // Добавить отзыв
    @PostMapping("/add")
    public String addReview(@RequestParam Long productId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            reviewService.addReview(userId, productId, rating, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Отзыв добавлен! Он появится после проверки модератором.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        return "redirect:/product/" + productId;
    }

    // Удалить отзыв
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Отзыв удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }

        return "redirect:/reviews/my";
    }

    // Мои отзывы
    @GetMapping("/my")
    public String myReviews(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("reviews", reviewService.getUserReviews(userId));
        model.addAttribute("loggedIn", true);
        model.addAttribute("userName", session.getAttribute("userName"));

        // Количество товаров в корзине
        String sessionId = cartService.getOrCreateSessionId(session);
        model.addAttribute("cartItemsCount", cartService.getCartItemsCount(sessionId));

        return "reviews/my-reviews";
    }
}