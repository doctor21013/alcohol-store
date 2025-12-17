//package com.alcoholstore.service;
//
//import com.alcoholstore.model.User;
//import com.alcoholstore.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Transactional
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder; // Оставляем для совместимости
//
//    public User registerUser(User user, String rawPassword) {
//        // Проверяем, существует ли пользователь с таким email
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            throw new RuntimeException("Пользователь с email " + user.getEmail() + " уже существует");
//        }
//
//        // Сохраняем пароль как есть (без хеширования)
//        user.setPassword(rawPassword);
//
//        // Устанавливаем роль по умолчанию
//        user.setRole("USER");
//        user.setEnabled(true);
//
//        return userRepository.save(user);
//    }
//
//    // Устаревший метод - теперь аутентификация через Spring Security
//    @Deprecated
//    public User login(String email, String password) {
//        return userRepository.findByEmail(email)
//                .filter(user -> password.equals(user.getPassword())) // Простое сравнение
//                .filter(user -> Boolean.TRUE.equals(user.getEnabled()))
//                .orElseThrow(() -> new RuntimeException("Неверный email или пароль"));
//    }
//
//    public boolean existsByEmail(String email) {
//        return userRepository.findByEmail(email).isPresent();
//    }
//
//    // Метод для создания пользователя (без хеширования пароля)
//    public User createUser(String fullName, String email, String password, String phone) {
//        // Проверяем, существует ли пользователь
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new RuntimeException("Пользователь с email " + email + " уже существует");
//        }
//
//        User user = new User();
//        user.setFullName(fullName);
//        user.setEmail(email);
//        user.setPassword(password); // Сохраняем пароль как есть
//        user.setPhone(phone);
//        user.setRole("USER");
//        user.setEnabled(true);
//
//        return userRepository.save(user);
//    }
//    // Получить всех пользователей
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    // Получить пользователя по ID
//    public Optional<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }
//
//    // Получить пользователя по email
//    public Optional<User> getUserByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
//    public User updateUser(Long id, User userDetails) {
//        return userRepository.findById(id).map(user -> {
//            user.setFullName(userDetails.getFullName());
//            user.setEmail(userDetails.getEmail());
//            user.setPhone(userDetails.getPhone());
//            user.setRole(userDetails.getRole());
//            user.setEnabled(userDetails.getEnabled());
//
//            // Если пришел новый пароль - сохраняем как есть
//            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
//                user.setPassword(userDetails.getPassword());
//            }
//
//            return userRepository.save(user);
//        }).orElse(null);
//    }
//
//    public void deleteUser(Long id) {
//        userRepository.deleteById(id);
//    }
//
//    public void toggleUserStatus(Long id) {
//        userRepository.findById(id).ifPresent(user -> {
//            user.setEnabled(!user.getEnabled());
//            userRepository.save(user);
//        });
//    }
//
//    public void changeUserRole(Long id, String role) {
//        userRepository.findById(id).ifPresent(user -> {
//            user.setRole(role);
//            userRepository.save(user);
//        });
//    }
//
//    public void changePassword(Long id, String rawPassword) {
//        userRepository.findById(id).ifPresent(user -> {
//            user.setPassword(rawPassword); // Сохраняем как есть
//            userRepository.save(user);
//        });
//    }
//
//    public List<User> getUsersByRole(String role) {
//        return userRepository.findByRole(role);
//    }
//
//    public long getTotalUsersCount() {
//        return userRepository.count();
//    }
//}
package com.alcoholstore.service;

import com.alcoholstore.model.User;
import com.alcoholstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Самый простой метод создания пользователя
    public User createUser(String fullName, String email, String password, String phone) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password); // Сохраняем как есть
        user.setPhone(phone);
        user.setRole("USER");
        user.setEnabled(true);

        return userRepository.save(user);
    }

    // В UserService.java добавь этот метод:
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(userDetails.getFullName());
            user.setEmail(userDetails.getEmail());
            user.setPhone(userDetails.getPhone());

            // Если пришел новый пароль (не пустой), то обновляем его
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(userDetails.getPassword());
            }

            return userRepository.save(user);
        }).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long getTotalUsersCount() {
        return userRepository.count();
    }

    public void toggleUserStatus(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(!user.getEnabled());
            userRepository.save(user);
        });
    }

    public void changeUserRole(Long id, String role) {
        userRepository.findById(id).ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
    }
}