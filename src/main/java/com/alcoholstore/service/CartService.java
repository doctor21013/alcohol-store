package com.alcoholstore.service;

import com.alcoholstore.model.*;
import com.alcoholstore.repository.CartRepository;
import com.alcoholstore.repository.CartItemRepository;
import com.alcoholstore.repository.ProductRepository;
import com.alcoholstore.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Метод для работы с сессией (для AboutController)
    public String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            sessionId = session.getId();
            session.setAttribute("sessionId", sessionId);
        }
        return sessionId;
    }

    // Метод для получения количества товаров в корзине через сессию
    public int getCartItemsCount(String username) {
        // Временная реализация - возвращаем 0
        // В реальном приложении здесь будет логика работы с сессией
        return 0;
    }

    // Получить или создать корзину для пользователя
    @Transactional
    public Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    // Получить количество товаров в корзине пользователя
    public int getCartItemCount(String username) {
        try {
            Cart cart = getOrCreateCart(username);
            return cart.getTotalItemsCount();
        } catch (Exception e) {
            return 0;
        }
    }


    // Получить содержимое корзины пользователя
    public Cart getCart(String username) {
        return getOrCreateCart(username);
    }

    // Добавить товар в корзину
    @Transactional
    public void addToCart(String username, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным");
        }

        Cart cart = getOrCreateCart(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // Проверяем, есть ли уже товар в корзине
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            // Обновляем количество
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Добавляем новый товар
            CartItem newItem = new CartItem(cart, product, quantity);
            cartItemRepository.save(newItem);
        }
    }

    // Удалить товар из корзины
    @Transactional
    public void removeFromCart(String username, Long productId) {
        Cart cart = getOrCreateCart(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresent(cartItemRepository::delete);
    }

    // Обновить количество товара
    @Transactional
    public void updateQuantity(String username, Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(username, productId);
            return;
        }

        Cart cart = getOrCreateCart(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresent(cartItem -> {
                    cartItem.setQuantity(quantity);
                    cartItemRepository.save(cartItem);
                });
    }

    // Очистить корзину
    @Transactional
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        cartItemRepository.deleteByCart(cart);
    }
}