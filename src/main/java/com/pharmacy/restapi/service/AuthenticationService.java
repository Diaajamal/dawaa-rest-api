package com.pharmacy.restapi.service;

import com.pharmacy.restapi.config.JwtService;
import com.pharmacy.restapi.model.*;
import com.pharmacy.restapi.payload.request.LoginRequest;
import com.pharmacy.restapi.payload.request.RegisterRequest;
import com.pharmacy.restapi.payload.response.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> userRegistration(RegisterRequest request) {
        return register(request, Role.USER);
    }

    public ResponseEntity<?> adminRegistration(RegisterRequest request) {
        return register(request, Role.ADMIN);
    }

    public ResponseEntity<?> register(RegisterRequest request, Role role) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userName(request.getUserName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .cart(new Cart())
                .role(role)
                .build();
        try {
            userService.save(user);
            return new ResponseEntity<>("User Created Successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("user already exists", HttpStatus.CONFLICT);
        }
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );
        var user = userService.findUser(request.getUserName());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .role(user.getRole().toString())
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userName;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.replace(TOKEN_PREFIX, "");
        try {
            userName = jwtService.extractUserName(refreshToken);
            if (userName != null) {
                var user = this.userService.findUser(userName);
                if (jwtService.isTokenValid(refreshToken)) {
                    var accessToken = jwtService.generateToken(user);
                    var authResponse = AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .role(user.getRole().toString())
                            .build();
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }
            }
        } catch (Exception e) {
            System.out.println("cant refresh token: " + e.getMessage());
        }
    }
}
