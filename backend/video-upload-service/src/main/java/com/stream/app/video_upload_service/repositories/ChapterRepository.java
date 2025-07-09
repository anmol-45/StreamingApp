package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    @Query("SELECT MAX(c.serialNumber) FROM Chapter c WHERE c.subject.subjectId = :subjectId")
    Integer findMaxSerialNumberBySubjectId(@Param("subjectId") Integer subjectId);
}
