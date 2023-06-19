package com.pharmacy.restapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false)
    private UUID id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int quantity;
    private UUID seller;
    private String category;
    private boolean inStock;


}
