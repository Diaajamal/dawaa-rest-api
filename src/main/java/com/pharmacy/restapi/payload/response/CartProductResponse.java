package com.pharmacy.restapi.payload.response;

import com.pharmacy.restapi.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartProductResponse {
    private Product product;
    private int quantity;
    private double total;
}
