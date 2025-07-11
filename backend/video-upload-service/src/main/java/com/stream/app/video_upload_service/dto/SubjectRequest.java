package com.stream.app.video_upload_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectRequest {
    private Integer courseId;
    private String subjectName;
    private  String subjectDescription;
}
