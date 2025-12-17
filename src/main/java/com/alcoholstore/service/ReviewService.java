package com.alcoholstore.service;

import com.alcoholstore.model.Product;
import com.alcoholstore.model.Review;
import com.alcoholstore.model.User;
import com.alcoholstore.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // Добавить отзыв
    public Review addReview(Long userId, Long productId, Integer rating, String comment) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }
        User user = userOpt.get();

        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Товар не найден");
        }
        Product product = productOpt.get();

        // Проверяем, не оставлял ли уже пользователь отзыв
        Optional<Review> existingReview = reviewRepository.findByProductAndUser(product, user);
        if (existingReview.isPresent()) {
            throw new RuntimeException("Вы уже оставляли отзыв на этот товар");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        // Для администраторов отзыв сразу одобрен
        if ("ADMIN".equals(user.getRole())) {
            review.setIsApproved(true);
        }

        return reviewRepository.save(review);
    }

    // Обновить отзыв
    public Review updateReview(Long reviewId, Integer rating, String comment) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new RuntimeException("Отзыв не найден");
        }

        Review review = reviewOpt.get();
        review.setRating(rating);
        review.setComment(comment);
        review.setUpdatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    // Удалить отзыв - ЭТОТ МЕТОД ДОЛЖЕН БЫТЬ
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Отзыв не найден");
        }
        reviewRepository.deleteById(reviewId);
    }

    // Получить все отзывы для товара (включая неодобренные для админа)
    public List<Review> getProductReviews(Long productId, boolean adminView) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Товар не найден");
        }
        Product product = productOpt.get();

        if (adminView) {
            return reviewRepository.findByProduct(product);
        } else {
            return reviewRepository.findByProductAndIsApprovedTrue(product);
        }
    }

    // Получить средний рейтинг товара
    public Double getProductAverageRating(Long productId) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return 0.0;
        }
        Product product = productOpt.get();

        Double average = reviewRepository.findAverageRatingByProduct(product);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

    // Получить количество отзывов
    public Long getProductReviewCount(Long productId) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return 0L;
        }
        Product product = productOpt.get();

        return reviewRepository.countApprovedReviewsByProduct(product);
    }

    // Одобрить отзыв (для админа)
    public Review approveReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new RuntimeException("Отзыв не найден");
        }

        Review review = reviewOpt.get();
        review.setIsApproved(true);
        return reviewRepository.save(review);
    }

    // Отклонить отзыв (для админа)
    public void rejectReview(Long reviewId) {
        deleteReview(reviewId);
    }

    // Получить все неодобренные отзывы (для админа)
    public List<Review> getUnapprovedReviews() {
        return reviewRepository.findByIsApprovedFalse();
    }

    // Получить отзывы пользователя
    public List<Review> getUserReviews(Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        User user = userOpt.get();

        return reviewRepository.findByUser(user);
    }
}