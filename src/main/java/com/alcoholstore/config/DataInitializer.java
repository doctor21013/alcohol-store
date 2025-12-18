package com.alcoholstore.config;

import com.alcoholstore.model.User;
import com.alcoholstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🔧 ИНИЦИАЛИЗАЦИЯ И ПРОВЕРКА ПОЛЬЗОВАТЕЛЕЙ...");

        // Проверяем существование пользователей и создаем только если их нет
        createUserIfNotExists("admin", "admin@alcoholstore.ru", "ROLE_ADMIN", "admin123");
        createUserIfNotExists("user", "user@example.com", "ROLE_USER", "user123");

        System.out.println("✅ ИНИЦИАЛИЗАЦИЯ ЗАВЕРШЕНА!");
    }

    private void createUserIfNotExists(String username, String email, String role, String password) {
        // Проверяем, существует ли пользователь с таким email
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            System.out.println("👤 Создание пользователя: " + username);

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setRole(role);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);

            userRepository.save(user);
            System.out.println("✅ Пользователь " + username + " создан успешно!");
        } else {
            System.out.println("ℹ️ Пользователь с email " + email + " уже существует, пропускаем создание.");
        }
    }
}