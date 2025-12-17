package com.alcoholstore.service;

import com.alcoholstore.model.*;
import com.alcoholstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.alcoholstore.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Получить все заказы
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    // Получить заказ по ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Обновить статус заказа
    public void updateOrderStatus(Long id, String status) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }

    // Получить последние заказы
    public List<Order> getRecentOrders(int count) {
        return orderRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // Сохранить заказ
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // Удалить заказ
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // Получить заказы по пользователю (синоним для getOrdersByUser)
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(email);
    }

    // Получить заказы по пользователю (старое название)
    public List<Order> getOrdersByUser(String email) {
        return getOrdersByEmail(email);
    }

    // Получить заказы по статусу
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    // Создать заказ из корзины - ЭТОТ МЕТОД ВАМ НУЖЕН
    public Order createOrderFromCart(String sessionId, String customerName,
                                     String customerEmail, String customerPhone,
                                     String deliveryAddress, String notes) {

        // Получаем товары из корзины
        List<CartItem> cartItems = cartItemRepository.findBySessionId(sessionId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        // Рассчитываем общую сумму
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Создаем новый заказ
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setDeliveryAddress(deliveryAddress);
        order.setNotes(notes);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Генерируем номер заказа
        order.setOrderNumber("ORD-" + System.currentTimeMillis());

        // Создаем OrderItem для каждого CartItem
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        // Очищаем корзину
        cartItemRepository.deleteAllBySessionId(sessionId);

        return savedOrder;
    }

    // Получить общую статистику по заказам
    public BigDecimal getTotalRevenue() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Получить количество заказов по статусу
    public long getOrderCountByStatus(String status) {
        return orderRepository.findAll().stream()
                .filter(order -> status.equals(order.getStatus()))
                .count();
    }
}