package com.profitsoft.parser.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    private long id;
    private String name;

    @JsonProperty("category_id")
    private long categoryId;
    private double price;
    private String manufacturer;

    private String tags;

    public Product() {
    }

    public Product(long id, String name, String manufacturer, String tags) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public double getPrice() {
        return price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}