package com.stream.app.user_service.services.authService;


import com.stream.app.user_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenContextService {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public String getEmailFromToken() {
        String token = extractToken();
        return jwtUtil.extractUserEmail(token);
    }

    public String getRoleFromToken() {
        String token = extractToken();
        return jwtUtil.extractUserRole(token);
    }

    private String extractToken() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid or missing Authorization header");
    }
}
