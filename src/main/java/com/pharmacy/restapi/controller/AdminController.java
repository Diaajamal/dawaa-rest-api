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

    @DeleteMapping(path = "/product/delete")
    public ResponseEntity<?> deleteProduct(HttpServletRequest request, @RequestParam UUID productId) {
        try {
            if (!adminService.checkOwnerShip(request, productId))
                return new ResponseEntity<>("you are not the owner of this product", HttpStatus.CONFLICT);
            adminService.deleteProduct(request, productId);
            return new ResponseEntity<>("product deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("product deletion failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping(path = "/product/update")
    public ResponseEntity<?> updateProduct(@RequestParam UUID oldProductId, @RequestBody ProductRequest newProduct) {
        try {
            adminService.updateProduct(oldProductId, newProduct);
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

    //upgrade user to seller
    @PostMapping(path = "/seller/add")
    public ResponseEntity<?> addSeller(@RequestParam String sellerUserName) {
        try {
            adminService.addSeller(sellerUserName);
            return new ResponseEntity<>("seller added successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("seller not found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    //downgrade seller to user
    @DeleteMapping(path = "/seller/delete")
    public ResponseEntity<?> deleteSeller(@RequestParam String sellerUserName) {
        try {
            adminService.deleteSeller(sellerUserName);
            return new ResponseEntity<>("seller deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("seller not found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
