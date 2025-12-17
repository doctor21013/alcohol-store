package com.alcoholstore.repository;

import com.alcoholstore.model.Favorite;
import com.alcoholstore.model.Product;
import com.alcoholstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user = :user AND f.product = :product")
    void deleteByUserAndProduct(@Param("user") User user, @Param("product") Product product);

    @Query("SELECT f.product FROM Favorite f WHERE f.user = :user")
    List<Product> findFavoriteProductsByUser(@Param("user") User user);

    int countByUser(User user);
}