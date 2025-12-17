package com.alcoholstore.service;

import com.alcoholstore.model.Category;
import com.alcoholstore.model.Product;
import com.alcoholstore.repository.CategoryRepository;
import com.alcoholstore.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    @Transactional
    public void initTestData() {
        try {
            // Проверяем, есть ли уже товары
            if (productRepository.count() == 0) {
                System.out.println("📦 Начинаем заполнение тестовыми данными...");

                // Найдите или создайте категории
                Category wine = categoryRepository.findByName("Вино")
                        .orElseGet(() -> {
                            Category cat = new Category();
                            cat.setName("Вино");
                            cat.setDescription("Элитные вина");
                            return categoryRepository.save(cat);
                        });

                Category beer = categoryRepository.findByName("Пиво")
                        .orElseGet(() -> {
                            Category cat = new Category();
                            cat.setName("Пиво");
                            cat.setDescription("Крафтовое пиво");
                            return categoryRepository.save(cat);
                        });

                Category spirits = categoryRepository.findByName("Крепкие напитки")
                        .orElseGet(() -> {
                            Category cat = new Category();
                            cat.setName("Крепкие напитки");
                            cat.setDescription("Виски, водка, коньяк, текила");
                            return categoryRepository.save(cat);
                        });

                Category champagne = categoryRepository.findByName("Шампанское")
                        .orElseGet(() -> {
                            Category cat = new Category();
                            cat.setName("Шампанское");
                            cat.setDescription("Игристое вино для праздников");
                            return categoryRepository.save(cat);
                        });

                Category nonAlcoholic = categoryRepository.findByName("Безалкогольное")
                        .orElseGet(() -> {
                            Category cat = new Category();
                            cat.setName("Безалкогольное");
                            cat.setDescription("Напитки без алкоголя");
                            return categoryRepository.save(cat);
                        });

                // ===== ВИНО =====
                createProduct("Château Margaux 2015",
                        "Элитное французское красное вино из региона Бордо. Имеет насыщенный вкус с нотами черной смородины, ванили и специй.",
                        new BigDecimal("45000.00"), 13.5, "Франция", 750, 15, wine,
                        "/images/products/wine1.jpg");

                createProduct("Sauvignon Blanc Cloudy Bay",
                        "Новозеландское белое вино с яркими нотами крыжовника и цитрусовых. Идеально к морепродуктам.",
                        new BigDecimal("7500.00"), 13.0, "Новая Зеландия", 750, 25, wine,
                        "/images/products/wine2.jpg");

                createProduct("Barolo DOCG 2018",
                        "Итальянское красное вино из винограда Неббиоло. Мощное, танинное, с нотами вишни и трюфелей.",
                        new BigDecimal("12000.00"), 14.5, "Италия", 750, 18, wine,
                        "/images/products/wine3.jpg");

                // ===== ПИВО =====
                createProduct("Guinness Draught",
                        "Классическое ирландское темное пиво с кремовой текстурой. Легендарный стаут с нотами кофе и шоколада.",
                        new BigDecimal("450.00"), 4.2, "Ирландия", 500, 120, beer,
                        "/images/products/beer1.jpg");

                createProduct("Hoegaarden White",
                        "Бельгийское пшеничное пиво с легкими цитрусовыми нотами. Освежающий вкус с пряными оттенками.",
                        new BigDecimal("380.00"), 4.9, "Бельгия", 500, 95, beer,
                        "/images/products/beer2.jpg");

                createProduct("IPA Siberian Corona",
                        "Российское крафтовое IPA с ярким хмелевым вкусом и нотами грейпфрута и сосны.",
                        new BigDecimal("520.00"), 6.5, "Россия", 500, 60, beer,
                        "/images/products/beer3.jpg");

                // ===== КРЕПКИЕ НАПИТКИ =====
                createProduct("Macallan 18 Years Sherry Oak",
                        "Односолодовый шотландский виски выдержкой 18 лет в хересных бочках. Богатый вкус с нотами сухофруктов и шоколада.",
                        new BigDecimal("85000.00"), 43.0, "Шотландия", 700, 8, spirits,
                        "/images/products/whisky1.jpg");

                createProduct("Beluga Noble Russian Vodka",
                        "Премиальная русская водка мягкого вкуса. Проходит тройную дистилляцию и фильтрацию через серебряные фильтры.",
                        new BigDecimal("3500.00"), 40.0, "Россия", 700, 42, spirits,
                        "/images/products/vodka1.jpg");

                createProduct("Hennessy X.O",
                        "Легендарный французский коньяк премиум-класса. Сложный букет с нотами ванили, специй и шоколада.",
                        new BigDecimal("28000.00"), 40.0, "Франция", 700, 22, spirits,
                        "/images/products/cognac1.jpg");

                // ===== ШАМПАНСКОЕ =====
                createProduct("Dom Pérignon Vintage 2012",
                        "Премиальное шампанское от Moët & Chandon. Элегантное, с нотами белых цветов, цитрусов и миндаля.",
                        new BigDecimal("25000.00"), 12.5, "Франция", 750, 12, champagne,
                        "/images/products/champagne1.jpg");

                createProduct("Prosecco DOC Treviso",
                        "Итальянское игристое вино с легкими фруктовыми нотами. Идеальный аперитив.",
                        new BigDecimal("2800.00"), 11.5, "Италия", 750, 35, champagne,
                        "/images/products/champagne2.jpg");

                // ===== БЕЗАЛКОГОЛЬНОЕ =====
                createProduct("Lavazza Qualità Rossa",
                        "Премиальный итальянский кофе в зернах. Насыщенный вкус с шоколадными нотами.",
                        new BigDecimal("1200.00"), 0.0, "Италия", 1000, 50, nonAlcoholic,
                        "/images/products/coffee1.jpg");

                createProduct("Twinings English Breakfast Tea",
                        "Классический английский чай. Бодрящий вкус с медовыми нотами.",
                        new BigDecimal("850.00"), 0.0, "Великобритания", 250, 65, nonAlcoholic,
                        "/images/products/tea1.jpg");

                System.out.println("✅ Тестовые данные созданы! Товаров: " + productRepository.count());
            } else {
                System.out.println("ℹ️ Данные уже существуют. Товаров: " + productRepository.count());
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка при создании данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Вспомогательный метод для создания товаров
    private void createProduct(String name, String description, BigDecimal price,
                               double alcoholContent, String countryOfOrigin, int volumeMl,
                               int inStock, Category category, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setAlcoholContent(alcoholContent);
        product.setCountryOfOrigin(countryOfOrigin);  // Используем countryOfOrigin
        product.setVolumeMl(volumeMl);                // Используем volumeMl
        product.setInStock(inStock);
        product.setCategory(category);
        product.setImageUrl(imageUrl);                // Используем переданный imageUrl

        productRepository.save(product);
    }

    // ===== ОСНОВНЫЕ МЕТОДЫ =====
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id); // Возвращаем Optional напрямую
    }
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}