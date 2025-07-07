package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
}
