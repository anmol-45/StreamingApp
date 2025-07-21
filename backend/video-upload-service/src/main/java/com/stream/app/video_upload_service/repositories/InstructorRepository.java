package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
}
