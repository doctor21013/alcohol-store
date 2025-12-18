package com.alcoholstore.repository;

import com.alcoholstore.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Метод для получения неодобренных отзывов
    List<Review> findByApprovedFalse();

    // Метод для получения отзывов по пользователю
    List<Review> findByUserId(Long userId);

    // Метод для получения отзывов по продукту
    List<Review> findByProductId(Long productId);

    // Метод для получения только одобренных отзывов по продукту
    List<Review> findByProductIdAndApprovedTrue(Long productId);

    // Метод для подсчета одобренных отзывов по продукту
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.approved = true")
    Long countByProductIdAndApprovedTrue(@Param("productId") Long productId);

    // Метод для подсчета всех отзывов по продукту
    Long countByProductId(Long productId);
}