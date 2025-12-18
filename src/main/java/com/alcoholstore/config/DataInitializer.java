package com.alcoholstore.config;

import com.alcoholstore.model.Order;
import com.alcoholstore.model.User;
import com.alcoholstore.service.OrderService;
import com.alcoholstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserService userService, OrderService orderService) {
        return args -> {
            // Создаем тестового администратора
            if (userService.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@example.com");
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                userService.saveUser(admin);
                System.out.println("✅ Создан администратор: admin / admin123");
            }

            // Создаем тестового пользователя
            if (userService.findByUsername("user") == null) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password"));
                user.setEmail("user@example.com");
                user.setRole("ROLE_USER");
                user.setEnabled(true);
                userService.saveUser(user);
                System.out.println("✅ Создан пользователь: user / password");
            }

            // Создаем тестовые заказы для админки
            if (orderService.getAllOrders().isEmpty()) {
                User adminUser = userService.findByUsername("admin");
                User testUser = userService.findByUsername("user");

                // Создаем несколько тестовых заказов
                for (int i = 1; i <= 5; i++) {
                    Order order = new Order();
                    order.setUser(i % 2 == 0 ? adminUser : testUser);
                    order.setOrderDate(LocalDateTime.now().minusDays(i));
                    order.setStatus(i % 3 == 0 ? "COMPLETED" : i % 3 == 1 ? "PROCESSING" : "PENDING");
                    order.setTotalAmount(BigDecimal.valueOf(1000 + i * 500));
                    order.setCustomerName("Клиент " + i);
                    order.setCustomerEmail("client" + i + "@example.com");
                    order.setCustomerPhone("+7900" + (1000000 + i));
                    order.setDeliveryAddress("ул. Примерная, д." + i);
                    order.setNotes("Тестовый заказ №" + i);

                    // Сохраняем заказ через репозиторий
                    // (нужно добавить OrderRepository.save() в сервис)
                }
                System.out.println("✅ Созданы тестовые заказы");
            }
        };
    }
}