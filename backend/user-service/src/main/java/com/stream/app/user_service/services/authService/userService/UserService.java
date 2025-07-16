package com.stream.app.user_service.services.authService.userService;

import com.stream.app.user_service.dto.UserInfo;
import com.stream.app.user_service.entities.User;
import com.stream.app.user_service.payload.CustomResponseMessage;
import com.stream.app.user_service.repositories.UserRepo;
import com.stream.app.user_service.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    public ResponseEntity<CustomResponseMessage<?>> updateDetails(UserInfo info, HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header: {}", header);
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Invalid Authorization header")
                            .data(null)
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );        }

        String token = header.substring(7);
        logger.info("Extracted token: {}", token);

        String userName = jwtUtil.extractUserName(token);
        if (userName == null) {
            logger.warn("Failed to extract username from token");
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Invalid token payload")
                            .data(null)
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        boolean isEmail = userName.contains("@");
        Optional<User> optionalUser = isEmail ? userRepo.findByEmail(userName) : userRepo.findByPhone(userName);

        if (optionalUser.isEmpty()) {
            logger.error("User not found for identifier: {}", userName);
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User not found")
                            .data(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        User user = optionalUser.get();
        logger.info("Updating user details for user: {}", userName);

        if (isEmail) {
            user.setPhone(info.getId());
            logger.info("Set phone number to: {}", info.getId());
        } else {
            user.setEmail(info.getId());
            logger.info("Set email to: {}", info.getId());
        }
        user.setName(info.getName());
        logger.info("Set name to: {}", info.getName());

        try {
            userRepo.save(user);
            logger.info("User details updated successfully for user: {}", userName);
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User details updated successfully")
                            .data(null)
                            .build(),
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            logger.error("Error saving user details: {}", ex.getMessage(), ex);
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Failed to update user details")
                            .data(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
