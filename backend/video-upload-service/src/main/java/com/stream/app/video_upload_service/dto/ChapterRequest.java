package com.stream.app.video_upload_service.dto;

import lombok.Data;

@Data
public class ChapterRequest {
    private Integer subjectId;
    private String chapterName;
    private String chapterDescription;
}
