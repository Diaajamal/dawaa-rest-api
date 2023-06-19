package com.pharmacy.restapi.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private String category;
    private int quantity;
    private UUID sellerId;
}
