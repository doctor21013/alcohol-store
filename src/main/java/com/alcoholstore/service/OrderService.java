package com.alcoholstore.service;

import com.alcoholstore.model.*;
import com.alcoholstore.repository.OrderRepository;
import com.alcoholstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // Создать заказ из корзины
    @Transactional
    public Order createOrderFromCart(Cart cart,
                                     String customerName,
                                     String customerEmail,
                                     String customerPhone,
                                     String deliveryAddress,
                                     String notes) {

        // Создаем новый заказ
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setDeliveryAddress(deliveryAddress);
        order.setNotes(notes);

        // Если есть авторизованный пользователь, связываем с ним
        if (cart.getUser() != null) {
            order.setUser(cart.getUser());
        }

        // Копируем товары из корзины в заказ
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            // Вычисляем общую сумму
            BigDecimal itemTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // Сохраняем заказ
        return orderRepository.save(order);
    }

    // Получить заказы пользователя
    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return orderRepository.findByUserOrderByOrderDateDesc(user);
        }
        // Если пользователь не найден, попробуем найти по email
        return orderRepository.findByCustomerEmailOrderByOrderDateDesc(username);
    }

    // Получить все заказы (для админа)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    // Получить недавние заказы (для дашборда)
    public List<Order> getRecentOrders(int count) {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }

    // Обновить статус заказа
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    // Получить заказ по ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}