package com.stream.app.user_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResponseDto {

    private String message;
    private String loginId;
}
