package com.pharmacy.restapi.service;

import com.pharmacy.restapi.model.Cart;
import com.pharmacy.restapi.model.CartProduct;
import com.pharmacy.restapi.model.Product;
import com.pharmacy.restapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CartProductService cartProductService;


    public void save(final Product product) {
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            List<CartProduct> cartProducts = cartProductService.findByProductId(productId);

            for (CartProduct cartProduct : cartProducts) {
                int quantity = cartProduct.getQuantity();
                Cart cart = cartProduct.getCart();
                cart.setQuantity(cart.getQuantity() - quantity);
                cartProductService.delete(cartProduct);
            }
            productRepository.delete(product);
        }
    }

    public Product findProduct(final UUID id) {
        if (id == null) return null;
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    public List<Product> findProductsThatContains(String text) {
        return productRepository.findProductsThatContains(text);
    }

    public List<Product> getByPriceBetween(double min, double max) {
        return productRepository.findByPriceBetween(min, max);
    }

    public List<Product> getByPriceGreaterThanEqual(double min) {
        return productRepository.findByPriceGreaterThanEqual(min);
    }

    public List<Product> getByPriceLessThanEqual(double max) {
        return productRepository.findByPriceLessThanEqual(max);
    }

    public List<Product> getBySellerId(UUID sellerId) {
        return productRepository.findProductsBySellerId(sellerId);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

}
