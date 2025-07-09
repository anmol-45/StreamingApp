package com.stream.app.video_upload_service.dto;

import com.stream.app.video_upload_service.entities.ContentType;
import lombok.Data;

import javax.swing.text.AbstractDocument;

@Data
public class LectureRequest {
    private Integer chapterId;
    private String lectureName;
    private String lectureDescription;
    private ContentType lectureType;
    private String lectureUrl;

}
