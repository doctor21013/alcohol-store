package com.alcoholstore.repository;

import com.alcoholstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    // УДАЛЕНО: List<Product> findByCategoryId(Long categoryId);
    List<Product> findByInStockGreaterThan(Integer quantity);
    List<Product> findTop8ByOrderByIdDesc();
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
}