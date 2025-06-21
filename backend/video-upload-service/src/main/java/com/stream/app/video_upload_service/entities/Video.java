package com.stream.app.video_upload_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stream_videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long VideoId;

    private String title;

    private String description;

    private String contentType;

    private String filePath;


}