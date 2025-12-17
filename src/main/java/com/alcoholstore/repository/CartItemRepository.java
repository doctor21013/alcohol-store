package com.alcoholstore.repository;

import com.alcoholstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Найти все элементы корзины по sessionId
    List<CartItem> findBySessionId(String sessionId);
    void deleteAllBySessionId(String sessionId);

    // Найти конкретный товар в корзине по sessionId и productId
    CartItem findBySessionIdAndProductId(String sessionId, Long productId);

    // Удалить все товары из корзины по sessionId
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);

    // Удалить конкретный товар из корзины по sessionId и productId
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.sessionId = :sessionId AND c.product.id = :productId")
    void deleteBySessionIdAndProductId(@Param("sessionId") String sessionId, @Param("productId") Long productId);

    // Посчитать количество товаров в корзине
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.sessionId = :sessionId")
    Integer countBySessionId(@Param("sessionId") String sessionId);

    // Получить общую сумму корзины
    @Query("SELECT SUM(c.quantity * c.product.price) FROM CartItem c WHERE c.sessionId = :sessionId")
    BigDecimal getTotalPriceBySessionId(@Param("sessionId") String sessionId);
}