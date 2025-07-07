package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.ChapterContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterContentRepository extends JpaRepository<ChapterContent, Integer> {
}
