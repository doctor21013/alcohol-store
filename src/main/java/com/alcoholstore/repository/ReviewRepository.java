package com.alcoholstore.repository;

import com.alcoholstore.model.Product;
import com.alcoholstore.model.Review;
import com.alcoholstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByProductAndUser(Product product, User user);

    List<Review> findByProduct(Product product);

    List<Review> findByProductAndIsApprovedTrue(Product product);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product AND r.isApproved = true")
    Double findAverageRatingByProduct(@Param("product") Product product);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = :product AND r.isApproved = true")
    Long countApprovedReviewsByProduct(@Param("product") Product product);

    List<Review> findByIsApprovedFalse();

    List<Review> findByUser(User user);
}