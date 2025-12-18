package com.alcoholstore.repository;

import com.alcoholstore.model.Order;
import com.alcoholstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findByCustomerEmailOrderByOrderDateDesc(String email);
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findTop10ByOrderByOrderDateDesc();

    // Новые методы для статистики
    List<Order> findByStatusOrderByOrderDateDesc(String status);
    List<Order> findByOrderDateAfter(LocalDateTime date);

    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE")
    long countOrdersToday();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE")
    BigDecimal sumRevenueToday();
}