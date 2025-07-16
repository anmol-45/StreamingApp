package com.stream.app.video_upload_service.controller;

import com.stream.app.video_upload_service.dto.ChapterRequest;
import com.stream.app.video_upload_service.dto.CourseRequest;
import com.stream.app.video_upload_service.dto.SubjectRequest;
import com.stream.app.video_upload_service.entities.ContentType;
import com.stream.app.video_upload_service.payload.CustomResponseMessage;
import com.stream.app.video_upload_service.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/courses")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CustomResponseMessage<?>> createCourse(@RequestBody CourseRequest courseRequest) {
        log.info("Received course creation request: {}", courseRequest);
        try {
            return courseService.createCourse(courseRequest);
        } catch (Exception e) {
            log.error("Error while creating course", e);
            return new ResponseEntity<>(CustomResponseMessage.builder()
                    .message("Failed to create course with error: "+ e.getMessage())
                    .data(null)
                    .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseId}/subjects")
    public ResponseEntity<CustomResponseMessage<?>>createSubject(@PathVariable Integer courseId, @RequestBody SubjectRequest subjectRequest) {
        log.info("Received subject creation request for courseId {}: {}", courseId, subjectRequest);
        subjectRequest.setCourseId(courseId);
        try {
            return courseService.createSubject(subjectRequest);
        } catch (Exception e) {
            log.error("Error while creating subject", e);
            return new ResponseEntity<>(CustomResponseMessage.builder()
                    .message("Failed to create subject with error: "+ e.getMessage())
                    .data(null)
                    .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/subjects/{subjectId}/chapters")
    public ResponseEntity<CustomResponseMessage<?>> createChapter(@PathVariable Integer subjectId, @RequestBody ChapterRequest chapterRequest) {
        log.info("Received chapter creation request for subjectId {}: {}", subjectId, chapterRequest);
        chapterRequest.setSubjectId(subjectId);
        try {
            return courseService.createChapter(chapterRequest);
        } catch (Exception e) {
            log.error("Error while creating chapter", e);
            return new ResponseEntity<>(CustomResponseMessage.builder()
                    .message("Failed to create chapter with error: "+ e.getMessage())
                    .data(null)
                    .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/chapters/{chapterId}/lectures")
//    public ResponseEntity<?> createLecture(@PathVariable Integer chapterId, @RequestBody LectureRequest lectureRequest) {
//        log.info("Received lecture creation request for chapterId {}: {}", chapterId, lectureRequest);
//        lectureRequest.setChapterId(chapterId);
//        try {
//            return courseService.createLecture(lectureRequest);
//        } catch (Exception e) {
//            log.error("Error while creating lecture", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to create lecture");
//        }
//    }
//
//
//    @PostMapping("/{chapterId}/upload-video")
//    public ResponseEntity<?> uploadLecture(@PathVariable Integer chapterId,
//                                         @RequestParam("file") MultipartFile file,
//                                         @RequestParam("title") String title,
//                                         @RequestParam("description") String description) {
//        log.info("Received video upload request for chapterID {} with title {}", chapterId, title);
//        try {
//            return courseService.uploadLecture(chapterId, file, title, description);
//        } catch (Exception e) {
//            log.error("Error while uploading video", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to upload video");
//        }
//    }
        @PostMapping("/chapters/{chapterId}/lectures/upload")
        public ResponseEntity<CustomResponseMessage<?>> uploadAndSaveLecture(@PathVariable Integer chapterId,
                                                      @RequestParam("serialNo")Integer serialNo,
                                                      @RequestParam("file") MultipartFile file,
                                                      @RequestParam("title") String title,
                                                      @RequestParam("content") String content,
                                                      @RequestParam("contentType") ContentType contentType) {
            log.info("Received lecture upload + save request for chapterID {} with title {}", chapterId, title);
            try {
                return courseService.uploadAndSaveLecture(chapterId,serialNo, file, title, content, contentType);
            } catch (Exception e) {
                log.error("Error while uploading and saving lecture", e);
                return new ResponseEntity<>(CustomResponseMessage.builder()
                        .message("Failed to upload and save lecture with error: "+ e.getMessage())
                        .data(null)
                        .build(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

}
