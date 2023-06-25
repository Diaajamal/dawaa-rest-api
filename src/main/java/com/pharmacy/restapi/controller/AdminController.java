package com.pharmacy.restapi.controller;


import com.pharmacy.restapi.payload.request.ProductRequest;
import com.pharmacy.restapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin")
public class AdminController {
    private final UserService adminService;

    @GetMapping
    public String adminGreetings() {
        return "Hello admin from a secured endpoint";
    }

    @PostMapping(path = "/product/create")
    public ResponseEntity<?> createProduct(HttpServletRequest request, @RequestBody ProductRequest productRequest) {
        try {
            adminService.createProduct(request, productRequest);
            return new ResponseEntity<>("product created successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("product creation failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(path = "/product/delete/{id}")
    public ResponseEntity<?> deleteProduct(HttpServletRequest request, @PathVariable("id") UUID productId) {
        try {
            if (!adminService.checkOwnerShip(request, productId))
                return new ResponseEntity<>("you are not the owner of this product", HttpStatus.CONFLICT);
            adminService.deleteProduct(request, productId);
            return new ResponseEntity<>("product deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("product deletion failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping(path = "/product/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") UUID oldProductId, @RequestBody ProductRequest updatedProduct) {
        try {
            adminService.updateProduct(oldProductId, updatedProduct);
            return new ResponseEntity<>("product updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("product updating failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
    @GetMapping(path = "/seller/products")
    public ResponseEntity<?> getSellerProducts(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(adminService.getSellerProducts(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    //upgrade a user to an admin
    @PostMapping(path = "/seller/add/{userName}")
    public ResponseEntity<?> upgradeToAdmin(@PathVariable("userName") String userName) {
        try {
            adminService.upgradeToAdmin(userName);
            return new ResponseEntity<>("user upgraded successfully to admin", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("user is not found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
