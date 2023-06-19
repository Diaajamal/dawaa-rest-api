package com.pharmacy.restapi.service;

import com.pharmacy.restapi.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX;
    @Value("${jwt.header}")
    public String HEADER_STRING;
    private final UserService userService;

    @Transactional
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(HEADER_STRING);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return;
        }
        User user = userService.getUserFromToken(request);
        if (user != null) {
            SecurityContextHolder.clearContext();
        }
    }
}
