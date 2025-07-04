package com.stream.app.user_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoggedInUserResponse {
    private String loginId;
    private String message;
    private String existingDevice;
    private String loggedInIp;
    private LocalDateTime existingLoginTime;
}
