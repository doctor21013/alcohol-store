package com.alcoholstore.repository;

import com.alcoholstore.model.Order;
import com.alcoholstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findByCustomerEmailOrderByOrderDateDesc(String email);
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findTop10ByOrderByOrderDateDesc();
}