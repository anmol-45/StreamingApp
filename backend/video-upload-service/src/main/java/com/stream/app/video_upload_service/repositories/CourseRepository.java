package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Courses,Integer > {
}
