package com.stream.app.user_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Login {

    @Id
    private String id; // email or phone as primary key
    private String userId;

    private String otp;
    private LocalDateTime otpGeneratedAt;

    private String device;
    private String ip;

    private LocalDateTime loggedInAt;
    private LocalDateTime loggedOutAt;
    private boolean isLoggedIn = false;

    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;
}
