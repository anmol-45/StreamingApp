package com.stream.app.video_upload_service.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stream.app.video_upload_service.dto.*;
import com.stream.app.video_upload_service.entities.*;
import com.stream.app.video_upload_service.repositories.LectureRepository;
import com.stream.app.video_upload_service.repositories.ChapterRepository;
import com.stream.app.video_upload_service.repositories.CourseRepository;
import com.stream.app.video_upload_service.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LectureRepository lectureRepository;
    private final Cloudinary cloudinary;
    private final SubjectRepository subjectRepository;

    @Transactional
    public ResponseEntity<?> createCourse(CourseRequest request) {
        Courses course = Courses.builder()
                .courseName(request.getCourseName())
                .description(request.getDescription())
                .price(request.getPrice())
                .createdAt(LocalDateTime.now())
                .build();

        log.debug("Saving course: {}", course);
        Courses savedCourse = courseRepository.save(course);
        log.info("Course saved with ID: {}", savedCourse.getCourseId());

        return ResponseEntity.ok(CourseResponse.builder()
                .CourseId(savedCourse.getCourseId())
                .message("Course created successfully at: " + savedCourse.getCreatedAt())
                .build());
    }

    @Transactional
    public ResponseEntity<?> createSubject(SubjectRequest request) {
        Courses course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));

        Subject subject = Subject.builder()
                .subjectName(request.getSubjectName())
                .description(request.getSubjectDescription())
                .createdAt(LocalDateTime.now())
                .course(course)
                .build();

        log.debug("Saving subject: {}", subject);
        Subject savedSubject = subjectRepository.save(subject);
        log.info("Subject saved with ID: {}", savedSubject.getSubjectId());

        return ResponseEntity.ok(SubjectResponse.builder()
                .SubjectId(savedSubject.getSubjectId())
                .message("Subject created successfully at: " + savedSubject.getCreatedAt())
                .build());
    }

    @Transactional
    public ResponseEntity<?> createChapter(ChapterRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + request.getSubjectId()));

        Integer maxSerialNumber = chapterRepository.findMaxSerialNumberBySubjectId(subject.getSubjectId());
        int nextSerialNumber = (maxSerialNumber != null ? maxSerialNumber : 0) + 1;

        Chapter chapter = Chapter.builder()
                .chapterName(request.getChapterName())
                .serialNumber(nextSerialNumber)
                .description(request.getChapterDescription())
                .createdAt(LocalDateTime.now())
                .subject(subject)
                .build();

        log.debug("Saving chapter: {}", chapter);
        Chapter savedChapter = chapterRepository.save(chapter);
        log.info("Chapter saved with ID: {}", savedChapter.getChapterId());

        return ResponseEntity.ok(ChapterResponse.builder()
                .chapterId(savedChapter.getChapterId())
                .chapterNo(savedChapter.getSerialNumber())
                .message("Chapter created successfully at: " + savedChapter.getCreatedAt())
                .build());
    }

//    public ResponseEntity<?> createLecture(LectureRequest request) {
//
//        Optional<Chapter> optionalChapter = chapterRepository.findById(request.getChapterId());
//        if(optionalChapter.isEmpty()) {
//            throw new RuntimeException("Chapter not found");
//        }
//        Lecture lecture = new Lecture();
//        lecture.setSerialNumber(optionalChapter.get().getChapterId()+1); //check how to do it
//        lecture.setLectureName(request.getLectureName());
//        lecture.setDescription(request.getLectureDescription());
//        lecture.setContentType(request.getLectureType());
//        lecture.setContentURL(request.getLectureUrl());
//        lecture.setChapter(optionalChapter.get());
//        lecture.setCreatedAt(LocalDateTime.now());
//
//        try{
//
//            log.debug("Saving Lecture: {}", lecture);
//            lectureRepository.save(lecture);
//
//            return new ResponseEntity<>(
//                    LectureResponse.builder()
//                            .lectureId(lecture.getLectureId())
//                            .lectureNo(lecture.getSerialNumber())
//                            .message("lecture created successfully at : " + lecture.getCreatedAt())
//                    , HttpStatus.OK);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public ResponseEntity<?> uploadLecture(Integer chapterId, MultipartFile file, String title, String Description) {
//        log.info("Starting upload for video title: {}", title);
//
//        try {
//            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
//                    "resource_type", "video"
//            ));
//
//            String url = (String) uploadResult.get("secure_url");
//            log.info("Video uploaded successfully to Cloudinary. URL: {}", url);
//
//            Chapter chapter = chapterRepository.findById(chapterId)
//                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
//
//
//            Lecture content = new Lecture();
//            content.setChapter(chapter);
//            content.setLectureName(title);
//            content.setContentType(ContentType.VIDEO);
//            content.setContentURL(url);
//            content.setSerialNumber(1);
//            content.setCreatedAt(LocalDateTime.now());
//
//
//            lectureRepository.save(content);
//
//            log.info("lecture saved successfully at URL: {}", url);
//            return new ResponseEntity<>("lecture saved successfully: \n contentId: " + content.getLectureId() + "\n url: "+ content.getContentURL() , HttpStatus.OK);
//
//        } catch (IOException e) {
//            log.error("Video upload failed due to IOException: {}", e.getMessage());
//            return null;
//        }
//    }
@Transactional
public ResponseEntity<?> uploadAndSaveLecture(Integer chapterId,
                                              MultipartFile file,
                                              String title,
                                              String description,
                                              ContentType contentType) {
    log.info("Starting upload for lecture: {}", title);

    try {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", contentType == ContentType.VIDEO ? "video" : "auto"
        ));
        String url = (String) uploadResult.get("secure_url");
        log.info("{} uploaded successfully. URL: {}", contentType, url);

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found with ID: " + chapterId));

        Integer maxSerialNumber = lectureRepository.findMaxSerialNumberByChapterId(chapterId);
        int nextSerialNumber = (maxSerialNumber != null ? maxSerialNumber : 0) + 1;

        Lecture lecture = Lecture.builder()
                .lectureName(title)
                .description(description)
                .contentType(contentType)
                .contentURL(url)
                .serialNumber(nextSerialNumber)
                .chapter(chapter)
                .createdAt(LocalDateTime.now())
                .build();

        lectureRepository.save(lecture);
        log.info("Lecture saved successfully with ID: {}", lecture.getLectureId());

        return ResponseEntity.ok(
                LectureResponse.builder()
                        .lectureId(lecture.getLectureId())
                        .lectureNo(lecture.getSerialNumber())
                        .message("Lecture created successfully with URL: " + url)
                        .build()
        );

    } catch (IOException e) {
        log.error("File upload failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload file due to IOException");
    }
}

}
