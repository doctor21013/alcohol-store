package com.alcoholstore.service;

import com.alcoholstore.model.CartItem;
import com.alcoholstore.model.Product;
import com.alcoholstore.repository.CartItemRepository;
import com.alcoholstore.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    // Генерировать ID сессии
    public String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute("cartSessionId");
        if (sessionId == null) {
            sessionId = "CART-" + System.currentTimeMillis() + "-" + session.getId();
            session.setAttribute("cartSessionId", sessionId);
        }
        return sessionId;
    }

    // Получить все товары в корзине
    public List<CartItem> getCartItems(String sessionId) {
        return cartItemRepository.findBySessionId(sessionId);
    }

    // Добавить товар в корзину
    public void addToCart(String sessionId, Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Проверяем, есть ли уже этот товар в корзине
            List<CartItem> existingItems = cartItemRepository.findBySessionId(sessionId);
            Optional<CartItem> existingItem = existingItems.stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst();

            if (existingItem.isPresent()) {
                // Обновляем количество
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
            } else {
                // Добавляем новый товар
                CartItem newItem = new CartItem();
                newItem.setSessionId(sessionId);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                cartItemRepository.save(newItem);
            }
        }
    }

    // Обновить количество товара
    public void updateQuantity(String sessionId, Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(sessionId, productId);
            return;
        }

        cartItemRepository.findBySessionId(sessionId).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    cartItemRepository.save(item);
                });
    }

    // Удалить товар из корзины
    public void removeFromCart(String sessionId, Long productId) {
        cartItemRepository.findBySessionId(sessionId).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> cartItemRepository.delete(item));
    }

    // Очистить корзину
    public void clearCart(String sessionId) {
        cartItemRepository.deleteAllBySessionId(sessionId);
    }

    // Получить общую стоимость
    public BigDecimal getTotalPrice(String sessionId) {
        List<CartItem> cartItems = cartItemRepository.findBySessionId(sessionId);
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Получить количество товаров в корзине
    public int getCartItemsCount(String sessionId) {
        return cartItemRepository.findBySessionId(sessionId).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}