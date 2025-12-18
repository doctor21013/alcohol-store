package com.alcoholstore.service;

import com.alcoholstore.model.User;
import com.alcoholstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Найти пользователя по имени пользователя (возвращает User)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Найти пользователя по email (возвращает User) - исправлено для работы с Optional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null); // Возвращаем null, если пользователь не найден
    }

    // Получить пользователя по ID (возвращает Optional, так как это стандартный метод JpaRepository)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Получить пользователя по ID или выбросить исключение
    public User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с ID: " + id));
    }


    // Получить всех пользователей
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Сохранить пользователя
    public User saveUser(User user) {
        // Если это новый пользователь и пароль не зашифрован, шифруем его
        if (user.getId() == null && user.getPassword() != null &&
                !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // Получить общее количество пользователей
    public long getTotalUsersCount() {
        return userRepository.count();
    }

    // Проверить существование пользователя по email
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent(); // Исправлено
    }

    // Проверить существование пользователя по username
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    // Удалить пользователя
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Получить пользователя по имени пользователя (возвращает User)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Создать нового пользователя
    public User createUser(String username, String password, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(true);
        return userRepository.save(user);
    }
}