package com.stream.app.video_upload_service.dto;

import com.stream.app.video_upload_service.entities.Instructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Integer courseId;
    private String courseName;
    private String courseSubTitle;
    private String description;
    private double price;
    private String imageUrl;

    private Instructor instructorResponse; // from Instructor table

    private List<String> bullets;
    private List<String> tags;
    private List<String> extras;
    private LocalDateTime createdAt;
}
