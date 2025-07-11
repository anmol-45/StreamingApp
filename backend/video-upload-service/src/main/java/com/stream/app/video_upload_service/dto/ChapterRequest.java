package com.stream.app.video_upload_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterRequest {
    private Integer subjectId;
    private String chapterName;
    private String chapterDescription;
}
