package com.pharmacy.restapi.controller;

import com.pharmacy.restapi.payload.request.LoginRequest;
import com.pharmacy.restapi.payload.request.RegisterRequest;
import com.pharmacy.restapi.payload.response.AuthenticationResponse;
import com.pharmacy.restapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService AuthService;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from an unsecured endpoint");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(AuthService.login(request));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerAsCustomer(@RequestBody RegisterRequest request) {
        return AuthService.userRegistration(request);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAsSeller(@RequestBody RegisterRequest request) {
        return AuthService.adminRegistration(request);
    }

}
