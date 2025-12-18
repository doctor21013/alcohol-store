package com.alcoholstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        System.out.println("✅ Приложение запущено: http://localhost:8080");
        System.out.println("👑 Админ панель: http://localhost:8080/admin");
        System.out.println("🔑 Логин: admin / Пароль: admin123");
    }
}