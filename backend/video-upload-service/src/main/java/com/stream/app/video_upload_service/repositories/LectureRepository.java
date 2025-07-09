package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    @Query("SELECT MAX(l.serialNumber) FROM Lecture l WHERE l.chapter.chapterId = :chapterId")
    Integer findMaxSerialNumberByChapterId(@Param("chapterId") Integer chapterId);
}
