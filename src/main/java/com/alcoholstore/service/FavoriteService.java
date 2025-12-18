package com.alcoholstore.service;

import com.alcoholstore.model.Favorite;
import com.alcoholstore.model.Product;
import com.alcoholstore.model.User;
import com.alcoholstore.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // Добавить в избранное
    public Favorite addToFavorites(Long userId, Long productId) {
        User user = userService.getUserByIdOrThrow(userId);
        Product product = productService.getProductByIdOrThrow(productId);

        // Проверяем, есть ли уже в избранном
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndProduct(user, product);
        if (existingFavorite.isPresent()) {
            return existingFavorite.get();
        }

        Favorite favorite = new Favorite(user, product);
        return favoriteRepository.save(favorite);
    }

    // Удалить из избранного
    public void removeFromFavorites(Long userId, Long productId) {
        User user = userService.getUserByIdOrThrow(userId);
        Product product = productService.getProductByIdOrThrow(productId);

        favoriteRepository.deleteByUserAndProduct(user, product);
    }

    // Получить все избранное пользователя
    public List<Product> getUserFavorites(Long userId) {
        User user = userService.getUserByIdOrThrow(userId);
        return favoriteRepository.findFavoriteProductsByUser(user);
    }

    // Проверить, находится ли товар в избранном
    public boolean isProductInFavorites(Long userId, Long productId) {
        User user = userService.getUserByIdOrThrow(userId);
        Product product = productService.getProductByIdOrThrow(productId);

        return favoriteRepository.existsByUserAndProduct(user, product);
    }

    // Получить количество избранных товаров
    public int getFavoriteCount(Long userId) {
        User user = userService.getUserByIdOrThrow(userId);
        return favoriteRepository.countByUser(user);
    }
}