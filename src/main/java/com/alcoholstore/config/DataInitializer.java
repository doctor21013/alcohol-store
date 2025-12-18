package com.alcoholstore.config;

import com.alcoholstore.model.User;
import com.alcoholstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Создаем администратора, если его нет
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@alcoholstore.ru");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setIsAdmin(true);
            admin.setFullName("Администратор Системы");
            admin.setPhone("+7 (999) 123-45-67");
            userRepository.save(admin);
            System.out.println("✅ Создан администратор: admin / admin123");
        }

        // Создаем тестового пользователя
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setIsAdmin(false);
            user.setFullName("Тестовый Пользователь");
            user.setPhone("+7 (888) 765-43-21");
            userRepository.save(user);
            System.out.println("✅ Создан тестовый пользователь: user / user123");
        }

        System.out.println("🎯 Для входа используйте:");
        System.out.println("   Админ: admin / admin123");
        System.out.println("   Пользователь: user / user123");
    }
}