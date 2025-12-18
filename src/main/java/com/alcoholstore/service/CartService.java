package com.alcoholstore.service;

import com.alcoholstore.model.*;
import com.alcoholstore.repository.CartRepository;
import com.alcoholstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    private static final String CART_SESSION_KEY = "cartId";

    // Получить корзину текущего пользователя
    public Cart getCurrentCart(HttpSession session) {
        Long userId = getCurrentUserId(session);

        if (userId != null) {
            // Зарегистрированный пользователь
            Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
            if (cartOpt.isPresent()) {
                return cartOpt.get();
            } else {
                // Создаем новую корзину
                User user = userService.getUserById(userId)
                        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            }
        } else {
            // Гостевой пользователь - используем сессию
            Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
            if (cartId != null) {
                return cartRepository.findById(cartId)
                        .orElseGet(() -> createGuestCart(session));
            } else {
                return createGuestCart(session);
            }
        }
    }

    // Создать корзину для гостя
    private Cart createGuestCart(HttpSession session) {
        Cart cart = new Cart();
        cart = cartRepository.save(cart);
        session.setAttribute(CART_SESSION_KEY, cart.getId());
        return cart;
    }

    // Добавить товар в корзину
    @Transactional
    public void addToCart(HttpSession session, Long productId, int quantity) {
        Cart cart = getCurrentCart(session);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // Проверяем наличие на складе
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Недостаточно товара на складе. Доступно: " + product.getStockQuantity());
        }

        // Добавляем товар в корзину
        cart.addItem(product, quantity);
        cartRepository.save(cart);
    }

    // Удалить товар из корзины
    @Transactional
    public void removeFromCart(HttpSession session, Long productId) {
        Cart cart = getCurrentCart(session);
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    // Обновить количество товара
    @Transactional
    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(session, productId);
            return;
        }

        Cart cart = getCurrentCart(session);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // Проверяем наличие на складе
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Недостаточно товара на складе. Доступно: " + product.getStockQuantity());
        }

        // Находим и обновляем товар
        CartItem item = cart.findItemByProduct(product);
        if (item != null) {
            item.setQuantity(quantity);
            cartRepository.save(cart);
        }
    }

    // Очистить корзину
    @Transactional
    public void clearCart(HttpSession session) {
        Cart cart = getCurrentCart(session);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // Получить ID текущего пользователя из сессии
    private Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null ? user.getId() : null;
    }

    // Получить количество товаров в корзине
    public int getCartItemCount(HttpSession session) {
        Cart cart = getCurrentCart(session);
        return cart.getTotalItems();
    }

    // Получить общую стоимость корзины
    public double getCartTotal(HttpSession session) {
        Cart cart = getCurrentCart(session);
        return cart.getTotalPrice();
    }

    // Добавьте эти методы в конец класса CartService:

    // Старый метод для совместимости
    public String getOrCreateSessionId(HttpSession session) {
        Cart cart = getCurrentCart(session);
        return String.valueOf(cart.getId());
    }

    // Старый метод для совместимости
    public int getCartItemsCount(String sessionId) {
        // Преобразуем sessionId обратно в Long (cartId)
        try {
            Long cartId = Long.parseLong(sessionId);
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Корзина не найдена"));
            return cart.getTotalItems();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Старый метод для совместимости
    public List<CartItem> getCartItems(String sessionId) {
        try {
            Long cartId = Long.parseLong(sessionId);
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Корзина не найдена"));
            return cart.getItems();
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
    }

    // Старый метод для совместимости
    public BigDecimal getTotalPrice(String sessionId) {
        try {
            Long cartId = Long.parseLong(sessionId);
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Корзина не найдена"));
            return BigDecimal.valueOf(cart.getTotalPrice());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    // Старый метод для совместимости
    public void clearCart(String sessionId) {
        try {
            Long cartId = Long.parseLong(sessionId);
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Корзина не найдена"));
            cart.getItems().clear();
            cartRepository.save(cart);
        } catch (NumberFormatException e) {
            // Игнорируем
        }
    }

}