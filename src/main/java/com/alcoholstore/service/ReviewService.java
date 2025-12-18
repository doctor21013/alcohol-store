package com.alcoholstore.service;

import com.alcoholstore.model.Product;
import com.alcoholstore.model.Review;
import com.alcoholstore.model.User;
import com.alcoholstore.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    public List<Review> getUnapprovedReviews() {
        return reviewRepository.findByApprovedFalse();
    }

    public void approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        review.setApproved(true);
        reviewRepository.save(review);
    }

    public void rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        reviewRepository.delete(review);
    }

    public Review addReview(Long productId, Long userId, Integer rating, String text) {
        User user = userService.getUserByIdOrThrow(userId);

        // Исправлено: используем getProductById и проверяем на null
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Товар не найден с ID: " + productId);
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setText(text);
        review.setApproved(false);

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // Метод для получения среднего рейтинга товара
    public Double getProductAverageRating(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndApprovedTrue(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }

    // Метод для получения количества отзывов товара
    public Long getProductReviewCount(Long productId) {
        return reviewRepository.countByProductIdAndApprovedTrue(productId);
    }

    // Метод для получения отзывов товара (только одобренные)
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdAndApprovedTrue(productId);
    }
}