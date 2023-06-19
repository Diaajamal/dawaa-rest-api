package com.pharmacy.restapi.repository;

import com.pharmacy.restapi.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProduct, UUID> {
    List<CartProduct> findByCartId(UUID cartId);
    void deleteByCartId(UUID cartId);


    List<CartProduct> findByProductId(UUID productId);
}
