package com.stream.app.video_upload_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LectureResponse {
    private Integer lectureId;
    private Integer lectureNo;
    private String message;
}
