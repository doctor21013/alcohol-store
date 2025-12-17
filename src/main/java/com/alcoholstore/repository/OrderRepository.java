package com.alcoholstore.repository;

import com.alcoholstore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmailOrderByCreatedAtDesc(String email);
    List<Order> findByCustomerEmail(String email);
    List<Order> findTop10ByOrderByCreatedAtDesc();
    List<Order> findAllByOrderByCreatedAtDesc(); // Для админки
    List<Order> findByStatusOrderByCreatedAtDesc(String status); // Для админки

    // Добавьте метод для поиска по статусу (если нужно)
    long countByStatus(String status);
}