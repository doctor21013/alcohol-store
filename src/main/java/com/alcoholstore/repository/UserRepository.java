package com.alcoholstore.repository;

import com.alcoholstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск пользователя по email
    Optional<User> findByEmail(String email);

    // Поиск пользователя по телефону (добавлен для регистрации)
    Optional<User> findByPhone(String phone);
}