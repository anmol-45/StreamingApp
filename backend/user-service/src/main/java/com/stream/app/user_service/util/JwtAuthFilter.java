    package com.stream.app.user_service.util;

    import com.stream.app.user_service.entities.User;
    import com.stream.app.user_service.repositories.UserRepo;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.JwtException;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.NonNull;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;
    import java.util.List;
    import java.util.Optional;

    @Slf4j
    @Component
    public class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final UserRepo userRepo;

        @Autowired
        public JwtAuthFilter(JwtUtil jwtUtil, UserRepo userRepo) {
            this.jwtUtil = jwtUtil;
            this.userRepo = userRepo;
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull FilterChain filterChain
        ) throws ServletException, IOException {

            final String authHeader = request.getHeader("Authorization");
            final String token;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.validateToken(token);
                String subject = claims.getSubject(); // email or phone
                String role = claims.get("role", String.class);

                log.info("🔑 JWT token subject: {}", subject);
                log.info("🔑 JWT token role: {}", role);

                if (subject != null && role != null) {
                    // Lookup user by email or phone
                    Optional<User> user = subject.contains("@")
                            ? userRepo.findByEmail(subject)
                            : userRepo.findByPhone(subject);

                    if (user.isPresent() && user.get().getRole().name().equalsIgnoreCase(role)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                subject,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                        return;
                    } else {
                        log.warn("❌ User not found or role mismatch for subject: {}", subject);
                    }
                }

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Forbidden: Invalid token or role mismatch");

            } catch (JwtException e) {
                log.error("❌ Invalid or expired JWT token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid or expired token");
            }
        }
    }
