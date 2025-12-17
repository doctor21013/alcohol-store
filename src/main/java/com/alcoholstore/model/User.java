package com.alcoholstore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "role")
    private String role = "USER";

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Column(nullable = false)
    private String phone;

    @Column
    private Boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private String address;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (username == null || username.isEmpty()) {
            username = email;
        }
        if (fullName == null || fullName.isEmpty()) {
            fullName = "Имя Фамилия";
        }
    }

    public User() {}

    public User(String firstName, String lastName, String email, String password,
                String phone, String address) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.fullName = firstName + " " + lastName;
        this.username = email;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDateTime getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFirstName() {
        if (fullName != null && fullName.contains(" ")) {
            return fullName.split(" ")[0];
        }
        return fullName;
    }

    public String getLastName() {
        if (fullName != null && fullName.contains(" ")) {
            String[] parts = fullName.split(" ");
            return parts.length > 1 ? parts[1] : "";
        }
        return "";
    }
}