package com.alcoholstore.controller;

import com.alcoholstore.model.Review;
import com.alcoholstore.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Страница всех отзывов
    @GetMapping
    public String getAllReviews(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "reviews/list";
    }

    // Форма добавления отзыва
    @GetMapping("/add/{productId}")
    public String showAddReviewForm(@PathVariable Long productId,
                                    Model model,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addFlashAttribute("error", "Для добавления отзыва нужно войти в систему");
            return "redirect:/login";
        }

        model.addAttribute("productId", productId);
        model.addAttribute("review", new Review());
        return "reviews/add";
    }

    // Добавление отзыва
    @PostMapping("/add")
    public String addReview(@RequestParam Long productId,
                            @RequestParam Integer rating,
                            @RequestParam String text,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addFlashAttribute("error", "Для добавления отзыва нужно войти в систему");
            return "redirect:/login";
        }

        try {
            // Получаем ID пользователя из сессии
            Long userId = (Long) ((com.alcoholstore.model.User) session.getAttribute("user")).getId();

            // Добавляем отзыв
            reviewService.addReview(productId, userId, rating, text);

            redirectAttributes.addFlashAttribute("success", "Отзыв добавлен! Он появится после проверки администратором.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении отзыва: " + e.getMessage());
        }

        return "redirect:/products/" + productId;
    }

    // Мои отзывы
    @GetMapping("/my")
    public String getMyReviews(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addFlashAttribute("error", "Для просмотра отзывов нужно войти в систему");
            return "redirect:/login";
        }

        try {
            Long userId = (Long) ((com.alcoholstore.model.User) session.getAttribute("user")).getId();
            List<Review> reviews = reviewService.getReviewsByUser(userId);
            model.addAttribute("reviews", reviews);
        } catch (Exception e) {
            model.addAttribute("reviews", List.of());
            model.addAttribute("error", "Не удалось загрузить отзывы");
        }

        return "reviews/my";
    }

    // Удаление отзыва
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addFlashAttribute("error", "Для удаления отзыва нужно войти в систему");
            return "redirect:/login";
        }

        try {
            reviewService.rejectReview(id); // Используем метод rejectReview для удаления
            redirectAttributes.addFlashAttribute("success", "Отзыв удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении отзыва: " + e.getMessage());
        }

        return "redirect:/reviews/my";
    }
}