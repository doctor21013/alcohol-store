package com.alcoholstore.service;

import com.alcoholstore.model.Product;
import com.alcoholstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class ProductInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли уже товары в базе
        if (productRepository.count() == 0) {
            List<Product> products = Arrays.asList(
                    // Пиво
                    new Product(
                            "Belgian Craft Beer",
                            "Ароматное бельгийское крафтовое пиво с фруктовыми нотами",
                            new BigDecimal("450.00"),
                            "Пиво",
                            "beer1.jpg",
                            6.5,
                            500,
                            "Бельгия"
                    ),
                    new Product(
                            "German Lager",
                            "Классическое немецкое светлое пиво с чистым вкусом",
                            new BigDecimal("380.00"),
                            "Пиво",
                            "beer2.jpg",
                            5.0,
                            500,
                            "Германия"
                    ),
                    new Product(
                            "IPA Craft Beer",
                            "Индийский светлый эль с выраженной хмельной горчинкой",
                            new BigDecimal("520.00"),
                            "Пиво",
                            "beer3.jpg",
                            6.8,
                            330,
                            "США"
                    ),

                    // Шампанское
                    new Product(
                            "French Champagne Brut",
                            "Классическое французское шампанское брют",
                            new BigDecimal("2500.00"),
                            "Шампанское",
                            "champagne1.jpg",
                            12.0,
                            750,
                            "Франция"
                    ),
                    new Product(
                            "Premium Rosé Champagne",
                            "Игристое розовое шампанское премиум-класса",
                            new BigDecimal("3200.00"),
                            "Шампанское",
                            "champagne2.jpg",
                            12.5,
                            750,
                            "Франция"
                    ),

                    // Кофейный ликёр
                    new Product(
                            "Coffee Liqueur",
                            "Ароматный кофейный ликёр для коктейлей и десертов",
                            new BigDecimal("1200.00"),
                            "Ликёры",
                            "coffee1.jpg",
                            20.0,
                            700,
                            "Италия"
                    ),

                    // Коньяк
                    new Product(
                            "XO Cognac",
                            "Выдержанный коньяк XO с богатым букетом",
                            new BigDecimal("4500.00"),
                            "Коньяк",
                            "cognac1.jpg",
                            40.0,
                            700,
                            "Франция"
                    ),

                    // Чайный напиток
                    new Product(
                            "Earl Grey Gin",
                            "Джин с ароматом бергамота и чая Эрл Грей",
                            new BigDecimal("1800.00"),
                            "Джин",
                            "tea1.jpg",
                            40.0,
                            700,
                            "Великобритания"
                    ),

                    // Водка
                    new Product(
                            "Premium Russian Vodka",
                            "Премиальная русская водка тройной фильтрации",
                            new BigDecimal("1500.00"),
                            "Водка",
                            "vodka1.jpg",
                            40.0,
                            1000,
                            "Россия"
                    ),

                    // Виски
                    new Product(
                            "Single Malt Whisky",
                            "Односолодовый шотландский виски с острова Айла",
                            new BigDecimal("3800.00"),
                            "Виски",
                            "whisky1.jpg",
                            43.0,
                            700,
                            "Шотландия"
                    ),

                    // Вино
                    new Product(
                            "Red Bordeaux",
                            "Красное сухое вино из региона Бордо",
                            new BigDecimal("2200.00"),
                            "Вино",
                            "wine1.jpg",
                            13.5,
                            750,
                            "Франция"
                    ),
                    new Product(
                            "White Chardonnay",
                            "Белое вино Шардоне с нотами дуба",
                            new BigDecimal("1900.00"),
                            "Вино",
                            "wine2.jpg",
                            13.0,
                            750,
                            "Франция"
                    ),
                    new Product(
                            "Italian Prosecco",
                            "Игристое итальянское Просекко",
                            new BigDecimal("1600.00"),
                            "Вино",
                            "wine3.jpg",
                            11.5,
                            750,
                            "Италия"
                    )
            );

            productRepository.saveAll(products);
            System.out.println("✅ Загружено 13 товаров в базу данных");
        }
    }
}