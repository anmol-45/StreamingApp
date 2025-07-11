package com.stream.app.video_upload_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRequest {
    private String courseName;
    private String description;
    private BigDecimal price;
}
