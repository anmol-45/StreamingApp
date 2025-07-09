package com.stream.app.video_upload_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapterResponse {
    private Integer chapterId;
    private Integer chapterNo;
    private String message;
}
