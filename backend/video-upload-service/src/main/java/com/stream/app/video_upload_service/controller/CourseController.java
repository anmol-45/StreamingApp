package com.stream.app.video_upload_service.controller;

import com.stream.app.video_upload_service.dto.CourseRequest;
import com.stream.app.video_upload_service.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest courseRequest) {
        log.info("Received course creation request: {}" , courseRequest);
        try {
            var savedCourse = courseService.createCourse(courseRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
        } catch (Exception e) {
            log.error("Error while creating course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create course");
        }
    }

    @PostMapping("/{chapterId}/upload-video")
    public ResponseEntity<?> uploadVideo(@PathVariable Integer chapterId,
                                         @RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title) {
        log.info("Received video upload request for chapterID {} with title {}", chapterId, title);
        try {
            String videoUrl = courseService.uploadVideo(chapterId, file, title);
            return ResponseEntity.ok().body("Video uploaded successfully: " + videoUrl);
        } catch (Exception e) {
            log.error("Error while uploading video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload video");
        }
    }
}
