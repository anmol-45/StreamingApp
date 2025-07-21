package com.stream.app.video_upload_service.entities;

import com.stream.app.video_upload_service.dto.InstructorRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "instructors")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer instructorId;

    private String name;

    private String title;

    private String experience;

    @Lob
    private String bio;
    private LocalDateTime createdAt;

}
