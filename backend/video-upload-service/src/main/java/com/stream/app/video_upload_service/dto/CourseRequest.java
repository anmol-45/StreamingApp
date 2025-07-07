package com.stream.app.video_upload_service.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data@Builder
public class CourseRequest {
    private String courseName;
    private String description;
    private BigDecimal price;
}
