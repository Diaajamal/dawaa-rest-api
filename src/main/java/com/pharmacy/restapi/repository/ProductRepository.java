package com.pharmacy.restapi.repository;

import com.pharmacy.restapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query(value = "select p from Product p where p.inStock = true")
    List<Product> findAvailableProducts();

    @Query(value = """
            select p from Product p
            where p.name like %:text% or p.description like %:text%
            """)
    List<Product> findProductsThatContains(String text);

    @Query(value = """
                        select p from Product p
                        where p.price between ?1 and ?2
            """)
    List<Product> findByPriceBetween(double min, double max);

    @Query(value = """
            select p from Product p
            where p.price >= ?1
            """)
    List<Product> findByPriceGreaterThanEqual(double min);

    @Query(value = """
            select p from Product p
            where p.price <= ?1
            """)
    List<Product> findByPriceLessThanEqual(double max);

    Optional<Product> findById(UUID id);

    List<Product> findByCategory(String category);

    @Query(value = " select p from Product p where p.seller = ?1")
    List<Product> findProductsBySellerId(UUID id);
}
