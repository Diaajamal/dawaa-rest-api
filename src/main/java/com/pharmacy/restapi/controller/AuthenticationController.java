package com.pharmacy.restapi.controller;

import com.pharmacy.restapi.model.Product;
import com.pharmacy.restapi.model.User;
import com.pharmacy.restapi.payload.request.LoginRequest;
import com.pharmacy.restapi.payload.request.RegisterRequest;
import com.pharmacy.restapi.payload.response.AuthenticationResponse;
import com.pharmacy.restapi.service.AuthenticationService;
import com.pharmacy.restapi.service.ProductService;
import com.pharmacy.restapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService AuthService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/test1")
    public ResponseEntity<String> sayHello() {
        RegisterRequest user1 = new RegisterRequest("John", "Doe", "user1", "user1", "123 Main St", "password");
        RegisterRequest user2 = new RegisterRequest("Jane", "Doe", "user2", "user2", "123 Main St", "password");
        RegisterRequest admin = new RegisterRequest("Admin", "Admin", "admin", "admin", "123 Main St", "password");

        try {
            AuthService.userRegistration(user1);
            AuthService.userRegistration(user2);
            AuthService.adminRegistration(admin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User admin1 = userService.findUser("admin");

        var product1 = Product.builder()
                .name("Product 1")
                .description("Product 1 description")
                .price(10.00)
                .imageUrl("https://picsum.photos/200/300")
                .category("Category 1")
                .quantity(10)
                .inStock(true)
                .seller(admin1.getId())
                .build();
        var product2 = Product.builder()
                .name("Product 2")
                .description("Product 2 description")
                .price(20.00)
                .imageUrl("https://picsum.photos/200/300")
                .category("Category 2")
                .quantity(5)
                .inStock(true)
                .seller(admin1.getId())
                .build();
        var product3 = Product.builder()
                .name("Product 3")
                .description("Product 3 description")
                .price(30.00)
                .imageUrl("https://picsum.photos/200/300")
                .category("Category 3")
                .quantity(10)
                .inStock(true)
                .seller(admin1.getId())
                .build();

        try {
            productService.save(product1);
            productService.save(product2);
            productService.save(product3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("First Test Passed");
    }



    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        System.out.println(request.toString());
        return ResponseEntity.ok(AuthService.login(request));
    }

    @PostMapping("/customer-register")
    public ResponseEntity<?> registerAsCustomer(@RequestBody RegisterRequest request) {
        return AuthService.userRegistration(request);
    }

    @PostMapping("/seller-register")
    public ResponseEntity<?> registerAsSeller(@RequestBody RegisterRequest request) {
        return AuthService.adminRegistration(request);
    }
}
