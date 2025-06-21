package com.stream.app.video_upload_service.services.videoServiceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stream.app.video_upload_service.entities.Video;
import com.stream.app.video_upload_service.repositories.VideoRepo;
import com.stream.app.video_upload_service.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VideoServiceImpl implements VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private VideoRepo videoRepo;

    @Override
    public Video saveVideo(Video video, MultipartFile file) {
        logger.info("Starting upload for video title: {}", video.getTitle());

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "video"
            ));

            String url = (String) uploadResult.get("secure_url");
            String contentType = file.getContentType();


            video.setContentType(contentType);
            video.setFilePath(url);

            videoRepo.save(video);

            logger.info("Video uploaded successfully to Cloudinary. URL: {}", url);

            return video;


        } catch (IOException e) {
            logger.error("Video upload failed due to IOException: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Video getVideo(String title) {
        logger.info("Fetching video with title: {}", title);
        return videoRepo.findByTitle(title).orElse(null);
    }

    @Override
    public List<Video> getAllVideos() {
        logger.info("Fetching all videos");
        return videoRepo.findAll();
    }

    @Override
    public Video getById(String id) {
        logger.info("Fetching video by ID: {}", id);
        return videoRepo.findById(Long.parseLong(id)).orElse(null);
    }
}