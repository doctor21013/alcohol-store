package com.alcoholstore.repository;

import com.alcoholstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // ✅ Простая реализация через default метод
    default boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
    List<User> findByRole(String role);
}
