package com.stream.app.video_upload_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectResponse {
    private Integer SubjectId;
    private String message;
}
