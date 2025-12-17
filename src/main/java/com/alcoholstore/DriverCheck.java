package com.alcoholstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

@Component
public class DriverCheck implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Проверка драйверов ===");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            System.out.println("Драйвер: " + driver.getClass().getName());
        }
        System.out.println("=========================");
    }
}