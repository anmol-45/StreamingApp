package com.stream.app.user_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String userId;
    private String refreshToken;
    private String name;
    private String role;
    private String email;
    private String phone;

}
