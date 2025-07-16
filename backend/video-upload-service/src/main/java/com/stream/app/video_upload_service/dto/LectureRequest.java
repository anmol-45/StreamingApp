package com.stream.app.video_upload_service.dto;

import com.stream.app.video_upload_service.entities.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LectureRequest {
    private Integer chapterId;
    private String lectureName;
    private String lectureDescription;
    private ContentType lectureType;
    private String lectureUrl;

}
