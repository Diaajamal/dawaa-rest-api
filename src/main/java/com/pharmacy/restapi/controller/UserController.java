package com.pharmacy.restapi.controller;

import com.pharmacy.restapi.model.Product;
import com.pharmacy.restapi.model.User;
import com.pharmacy.restapi.payload.request.UserRequest;
import com.pharmacy.restapi.service.AuthenticationService;
import com.pharmacy.restapi.service.ProductService;
import com.pharmacy.restapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;
    private final ProductService productService;
    private final AuthenticationService authService;

    @GetMapping("/test1")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello user from a secured endpoint");
    }

    @GetMapping("/get-cart-products")
    public ResponseEntity<?> getUserProducts(HttpServletRequest request) {
        try {
            User user = userService.getUserFromToken(request);
            return new ResponseEntity<>(userService.getUserProducts(user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("no products were found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get-products")
    public ResponseEntity<?> getProducts() {
        try {
            System.out.println(productService.getProducts());
            return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("no products were found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get-available-products")
    public ResponseEntity<?> getAvailableProducts() {
        try {
            return new ResponseEntity<>(productService.getAvailableProducts(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("no products were found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get-products-by-category")
    public ResponseEntity<?> getProductsByCategory(@RequestParam String category) {
        try {
            return new ResponseEntity<>(productService.getProductsByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("no products were found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/search-products")
    public ResponseEntity<?> searchProducts(@RequestParam String text) {
        try {
            return new ResponseEntity<>(productService.findProductsThatContains(text), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("no products were found\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/add-product")
    public ResponseEntity<?> addProductToCart(HttpServletRequest request, @RequestParam UUID productId) {
        try {
            User user = userService.getUserFromToken(request);
            Product product = productService.findProduct(productId);
            userService.addProductToCart(user, product);
            return new ResponseEntity<>("product added to cart successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("product addition to cart failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
    }

    @DeleteMapping("/delete-product")
    public ResponseEntity<?> deleteProductFromCart(HttpServletRequest request, @RequestParam UUID productId) {
        try {
            User user = userService.getUserFromToken(request);
            Product product = productService.findProduct(productId);
            userService.deleteProductFromCart(user, product);
            return new ResponseEntity<>("product deleted from cart successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("product deletion failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody UserRequest updatedUser) {
        try {
            userService.updateUser(request, updatedUser);
            return new ResponseEntity<>("user updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("user update failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(userService.getUser(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("user retrieval failed\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}

