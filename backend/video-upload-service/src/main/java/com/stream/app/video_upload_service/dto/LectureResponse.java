package com.stream.app.video_upload_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LectureResponse {
    private Integer lectureId;
    private Integer lectureNo;
    private String message;
}
