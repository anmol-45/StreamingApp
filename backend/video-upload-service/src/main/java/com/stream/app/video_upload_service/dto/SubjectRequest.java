package com.stream.app.video_upload_service.dto;

import lombok.Data;

@Data
public class SubjectRequest {
    private Integer courseId;
    private String subjectName;
    private  String subjectDescription;
}
