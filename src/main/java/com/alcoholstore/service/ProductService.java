package com.alcoholstore.service;

import com.alcoholstore.model.Product;
import com.alcoholstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Добавлен метод для выброса исключения, если товар не найден
    public Product getProductByIdOrThrow(Long id) {
        Product product = getProductById(id);
        if (product == null) {
            throw new RuntimeException("Товар не найден с ID: " + id);
        }
        return product;
    }
}