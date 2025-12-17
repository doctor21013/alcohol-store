package com.alcoholstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "alcohol_content")
    private Double alcoholContent;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;  // ⚠️ Это имя поля!

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @Column(name = "in_stock")
    private Integer inStock = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_category"))
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private boolean favorite = false;

    // Конструктор по умолчанию (БЕЗ ПАРАМЕТРОВ) - ОБЯЗАТЕЛЬНО для JPA
    public Product() {
    }

    // Конструктор с параметрами
    public Product(String name, String description, BigDecimal price,
                   double alcoholContent, String countryOfOrigin, int volumeMl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.alcoholContent = alcoholContent;
        this.countryOfOrigin = countryOfOrigin;  // Используем правильное имя
        this.volumeMl = volumeMl;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========== ГЕТТЕРЫ И СЕТТЕРЫ ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(Double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    // ⚠️ ВАЖНО: Используем getCountryOfOrigin, а не getCountry!
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    // ⚠️ ВАЖНО: Используем setCountryOfOrigin, а не setCountry!
    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    // ⚠️ УДАЛИТЕ этот метод - он не нужен и вызывает ошибку!
    // public void setCountry(String country) {
    //     this.country = country;  // Поля 'country' не существует!
    // }

    public Integer getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(Integer volumeMl) {
        this.volumeMl = volumeMl;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Дополнительные полезные методы
    public String getFormattedPrice() {
        return price != null ? price + " руб." : "Цена не указана";
    }

    public String getFormattedVolume() {
        return volumeMl != null ? volumeMl + " мл" : "Объем не указан";
    }

    public boolean isAvailable() {
        return inStock != null && inStock > 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", inStock=" + inStock +
                '}';
    }

    public void setCountry(String country) {
    }
}