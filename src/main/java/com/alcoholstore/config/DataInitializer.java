//package com.alcoholstore.config;
//
//import com.alcoholstore.model.User;
//import com.alcoholstore.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Создание администратора, если его нет
//        String adminEmail = "admin@example.com";
//        if (userRepository.findByEmail(adminEmail).isEmpty()) {
//            User admin = new User();
//            admin.setFullName("Администратор");
//            admin.setEmail(adminEmail);
//            admin.setPassword("admin123"); // Пароль в чистом виде
//            admin.setPhone("+71234567890");
//            admin.setRole("ADMIN");
//            admin.setEnabled(true);
//            userRepository.save(admin);
//            System.out.println("✅ Администратор создан: " + adminEmail + " / admin123");
//        }
//
//        // Создание тестового пользователя
//        String testEmail = "user@example.com";
//        if (userRepository.findByEmail(testEmail).isEmpty()) {
//            User user = new User();
//            user.setFullName("Тестовый Пользователь");
//            user.setEmail(testEmail);
//            user.setPassword("user123"); // Пароль в чистом виде
//            user.setPhone("+79876543210");
//            user.setRole("USER");
//            user.setEnabled(true);
//            userRepository.save(user);
//            System.out.println("✅ Тестовый пользователь создан: " + testEmail + " / user123");
//        }
//
//        System.out.println("Всего пользователей в БД: " + userRepository.count());
//    }
//}