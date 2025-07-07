package com.stream.app.video_upload_service.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class LectureContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lectureID;

    private Integer serialNumber;

    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private String contentURL;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapterID", nullable = false)
    private Chapter chapter;

    // Getters & Setters
}
