package com.alcoholstore.service;

import com.alcoholstore.model.*;
import com.alcoholstore.repository.*;
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
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    // Создать заказ из корзины пользователя
    @Transactional
    public Order createOrderFromCart(String username,
                                     String customerName,
                                     String customerEmail,
                                     String customerPhone,
                                     String deliveryAddress,
                                     String notes) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Cart cart = cartService.getOrCreateCart(username);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setCustomerName(customerName != null ? customerName : user.getUsername());
        order.setCustomerEmail(customerEmail != null ? customerEmail : user.getEmail());
        order.setCustomerPhone(customerPhone);
        order.setDeliveryAddress(deliveryAddress);
        order.setNotes(notes);
        order.setUser(user);

        // Копируем товары из корзины в заказ
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            BigDecimal itemTotal = cartItem.getTotalPrice();
            totalAmount = totalAmount.add(itemTotal);

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // Сохраняем заказ и очищаем корзину
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(username);

        return savedOrder;
    }

    // Получить заказы пользователя
    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return orderRepository.findByUserOrderByOrderDateDesc(user);
        }
        return List.of();
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