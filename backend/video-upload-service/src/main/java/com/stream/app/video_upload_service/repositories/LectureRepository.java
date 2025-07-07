package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
}
