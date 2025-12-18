package com.alcoholstore.repository;

import com.alcoholstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByIsAvailableTrue();

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();

    List<Product> findByNameContainingIgnoreCase(String name);

    // Добавьте этот метод для поиска по имени ИЛИ описанию
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    // Метод для получения последних товаров (для главной страницы)
    List<Product> findTop8ByOrderByIdDesc();
}