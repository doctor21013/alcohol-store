package com.alcoholstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "alcohol_percentage")
    private Double alcoholPercentage;

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @Column(name = "country")
    private String country;

    @Column(name = "image_filename")
    private String imageFilename;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 10;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Transient
    private Boolean favorite = false;

    // Конструкторы
    public Product() {}

    public Product(String name, String description, BigDecimal price,
                   String category, String imageFilename,
                   Double alcoholPercentage, Integer volumeMl,
                   String country) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageFilename = imageFilename;
        this.alcoholPercentage = alcoholPercentage;
        this.volumeMl = volumeMl;
        this.country = country;
    }



    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getAlcoholPercentage() { return alcoholPercentage; }
    public void setAlcoholPercentage(Double alcoholPercentage) { this.alcoholPercentage = alcoholPercentage; }

    public Integer getVolumeMl() { return volumeMl; }
    public void setVolumeMl(Integer volumeMl) { this.volumeMl = volumeMl; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getImageFilename() { return imageFilename; }
    public void setImageFilename(String imageFilename) { this.imageFilename = imageFilename; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Boolean getFavorite() { return favorite; }
    public void setFavorite(Boolean favorite) { this.favorite = favorite; }

    // Удобный метод для получения полного пути к изображению
    public String getImageUrl() {
        return "/images.products/" + imageFilename;
    }

    // Удобный метод для получения цены как Double
    public Double getPriceAsDouble() {
        return price != null ? price.doubleValue() : 0.0;
    }
}