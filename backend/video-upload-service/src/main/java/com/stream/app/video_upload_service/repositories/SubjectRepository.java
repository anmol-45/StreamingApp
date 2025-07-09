package com.stream.app.video_upload_service.repositories;

import com.stream.app.video_upload_service.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
