package com.stream.app.video_upload_service.controller;

import com.stream.app.video_upload_service.dto.VideoDto;
import com.stream.app.video_upload_service.entities.Video;
import com.stream.app.video_upload_service.payload.CustomMessage;
import com.stream.app.video_upload_service.services.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoUploadController {

    private static final Logger logger = LoggerFactory.getLogger(VideoUploadController.class);
    private final VideoService videoService;

    public VideoUploadController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<CustomMessage> saveVideo(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("title") String title,
                                                   @RequestParam("description") String description) {
        logger.info("Received request to upload video with title: {}", title);

        Video video = new Video();
        video   .setTitle(title);
        video.setDescription(description);

        Video savedVideo = videoService.saveVideo(video, file);

        if (savedVideo == null) {
            logger.error("Video failed to upload.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomMessage("Video upload failed.", false, null));
        }

        VideoDto dto = new VideoDto(
                savedVideo.getTitle(),
                savedVideo.getDescription(),
                savedVideo.getContentType(),
                savedVideo.getFilePath()
        );

        logger.info("Video uploaded successfully to Cloudinary. URL: {}", savedVideo.getFilePath());

        return ResponseEntity.ok(new CustomMessage(
                "Video uploaded successfully to Cloudinary",
                true,
                dto
        ));
    }



    @GetMapping
    public ResponseEntity<CustomMessage> getAllVideos() {
        logger.info("Received request to fetch all videos");

        List<VideoDto> videos = videoService.getAllVideos().stream()
                .map(video -> new VideoDto(
                        video.getTitle(),
                        video.getDescription(),
                        video.getContentType(),
                        video.getFilePath()
                )).collect(Collectors.toList());

        logger.info("Returning {} videos", videos.size());

        return ResponseEntity.ok(new CustomMessage(
                "Fetched all videos successfully",
                true,
                videos
        ));
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<CustomMessage> getVideoByTitle(@PathVariable String title) {
        logger.info("Received request to fetch video with title: {}", title);

        Video video = videoService.getVideo(title);

        if (video == null) {
            logger.warn("Video not found with title: {}", title);
            return new ResponseEntity<>(new CustomMessage(
                    "Video not found with title: " + title,
                    false,
                    null
            ), HttpStatus.NOT_FOUND);
        }

        logger.info("Video found: {}", video.getTitle());

        VideoDto dto = new VideoDto(
                video.getTitle(),
                video.getDescription(),
                video.getContentType(),
                video.getFilePath()
        );

        return ResponseEntity.ok(new CustomMessage(
                "Video fetched successfully by title",
                true,
                dto
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomMessage> getVideoById(@PathVariable String id) {
        logger.info("Received request to fetch video with ID: {}", id);

        Video video = videoService.getById(id);

        if (video == null) {
            logger.warn("Video not found with ID: {}", id);
            return new ResponseEntity<>(new CustomMessage(
                    "Video not found with ID: " + id,
                    false,
                    null
            ), HttpStatus.NOT_FOUND);
        }

        logger.info("Video found: {}", video.getTitle());

        VideoDto dto = new VideoDto(
                video.getTitle(),
                video.getDescription(),
                video.getContentType(),
                video.getFilePath()
        );

        return ResponseEntity.ok(new CustomMessage(
                "Video fetched successfully by ID",
                true,
                dto
        ));
    }


}