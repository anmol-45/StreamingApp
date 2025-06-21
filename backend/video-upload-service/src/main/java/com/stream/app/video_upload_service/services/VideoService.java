package com.stream.app.video_upload_service.services;

import com.stream.app.video_upload_service.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public interface VideoService {

    Video saveVideo(Video video , MultipartFile file);
    Video getVideo(String title);
    List<Video> getAllVideos();
    Video getById(String id);
}