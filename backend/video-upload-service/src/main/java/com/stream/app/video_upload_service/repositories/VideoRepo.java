package com.stream.app.video_upload_service.repositories;


import com.stream.app.video_upload_service.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {

    Optional<Video> findByTitle(String s);
}
