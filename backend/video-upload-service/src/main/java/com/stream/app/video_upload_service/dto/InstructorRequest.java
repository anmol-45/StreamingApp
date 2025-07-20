package com.stream.app.video_upload_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorRequest {
    private String name;
    private String title;
    private String experience;
    private String bio;
}
