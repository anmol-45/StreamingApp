package com.stream.app.video_upload_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {
    private String courseName;
    private String subTitle;
    private String description;
    private BigDecimal originalPrice;
    private int discountPercent;
    private String currency;
    private List<String> tags;
    private List<String> extras;
    private List<String> overviewBullets;

    private InstructorRequest instructor;

}
