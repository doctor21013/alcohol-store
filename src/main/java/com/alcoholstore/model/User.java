package com.alcoholstore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 255) // Достаточная длина для пароля
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(name = "role", nullable = false)
    private String role = "USER";

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "age_confirmed", nullable = false)
    private Boolean ageConfirmed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Конструкторы
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String email, String password, String fullName) {
        this();
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Геттеры и сеттеры...
    // (остаются без изменений, как в предыдущей версии)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAgeConfirmed() {
        return ageConfirmed;
    }

    public void setAgeConfirmed(Boolean ageConfirmed) {
        this.ageConfirmed = ageConfirmed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}