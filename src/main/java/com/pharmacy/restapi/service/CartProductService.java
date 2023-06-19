package com.pharmacy.restapi.service;

import com.pharmacy.restapi.model.CartProduct;
import com.pharmacy.restapi.repository.CartProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartProductService {
    private final CartProductRepository cartProductRepository;

    public void save(CartProduct cartProduct) {
        cartProductRepository.save(cartProduct);
    }

    public void saveAll(List<CartProduct> cartProducts) {
        cartProductRepository.saveAll(cartProducts);
    }

    public void delete(CartProduct cartProduct) {
        cartProductRepository.delete(cartProduct);
    }

    public List<CartProduct> findByProductId(UUID productId) {
        return cartProductRepository.findByProductId(productId);
    }
}
