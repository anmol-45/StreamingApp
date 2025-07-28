package com.stream.app.video_stream_service.repositories;

import com.stream.app.video_stream_service.entities.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    List<Lecture> findByChapter_ChapterId(Integer chapterId);
}