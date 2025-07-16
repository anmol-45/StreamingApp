package com.stream.app.video_upload_service.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stream.app.video_upload_service.dto.*;
import com.stream.app.video_upload_service.entities.*;
import com.stream.app.video_upload_service.payload.CustomResponseMessage;
import com.stream.app.video_upload_service.repositories.LectureRepository;
import com.stream.app.video_upload_service.repositories.ChapterRepository;
import com.stream.app.video_upload_service.repositories.CourseRepository;
import com.stream.app.video_upload_service.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    public ResponseEntity<CustomResponseMessage<?>> createCourse(CourseRequest request) {
        Courses course = Courses.builder()
                .courseName(request.getCourseName())
                .description(request.getDescription())
                .price(request.getPrice())
                .createdAt(LocalDateTime.now())
                .build();

        log.debug("Saving course: {}", course);
        Courses savedCourse = courseRepository.save(course);
        log.info("Course saved with ID: {}", savedCourse.getCourseId());

        return new ResponseEntity<>(
                CustomResponseMessage.builder()
                        .message("Course created successfully at: " + savedCourse.getCreatedAt())
                        .data(CourseResponse.builder()
                                .CourseId(savedCourse.getCourseId())
                        )
                        .build()
                , HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CustomResponseMessage<?>>  createSubject(SubjectRequest request) {
        Optional<Courses> optionalCourse = courseRepository.findById(request.getCourseId());

        if (optionalCourse.isEmpty()) {
            log.warn("❌ Course not found with ID: {}", request.getCourseId());
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Course not found with ID: " + request.getCourseId())
                            .data(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        Courses course = optionalCourse.get();

        Subject subject = Subject.builder()
                .subjectName(request.getSubjectName())
                .description(request.getSubjectDescription())
                .createdAt(LocalDateTime.now())
                .course(course)
                .build();

        log.debug("Saving subject: {}", subject);
        Subject savedSubject = subjectRepository.save(subject);
        log.info("Subject saved with ID: {}", savedSubject.getSubjectId());

        return new ResponseEntity<>(
                CustomResponseMessage.builder()
                        .message("Subject created successfully at: " + savedSubject.getCreatedAt())
                        .data(SubjectResponse.builder()
                                .SubjectId(savedSubject.getSubjectId())
                                .build()
                        )
                        .build()
                , HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CustomResponseMessage<?>>  createChapter(ChapterRequest request) {
        Optional<Subject> optionalSubject = subjectRepository.findById(request.getSubjectId());

        if (optionalSubject.isEmpty()) {
            log.warn("❌ Subject not found with ID: {}", request.getSubjectId());
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Course not found with ID: " + request.getSubjectId())
                            .data(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        Subject subject = optionalSubject.get();

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

        return new ResponseEntity<>(
                CustomResponseMessage.builder()
                        .message("Chapter created successfully at: " + savedChapter.getCreatedAt())
                        .data(ChapterResponse.builder()
                                .chapterId(savedChapter.getChapterId())
                                .serialNo(savedChapter.getSerialNumber())
                                .build()
                        )
                        .build()
                , HttpStatus.OK);
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
    public ResponseEntity<CustomResponseMessage<?>>  uploadAndSaveLecture(Integer chapterId,
                                                  Integer serialNo,
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

            Optional<Chapter> optionalChapter = chapterRepository.findById(chapterId);
            if (optionalChapter.isEmpty()) {
                log.warn("❌ Chapter not found with ID: {}", chapterId);
                return new ResponseEntity<>(
                        CustomResponseMessage.builder()
                                .message("Chapter not found with ID: " + chapterId)
                                .data(null)
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            Chapter chapter = optionalChapter.get();

            Integer maxSerialNumber = lectureRepository.findMaxSerialNumberByChapterId(chapterId);
            int nextSerialNumber = (maxSerialNumber != null ? maxSerialNumber : 0) + 1;

            Lecture lecture = Lecture.builder()
                    .lectureName(title)
                    .description(description)
                    .contentType(contentType)
                    .contentURL(url)
                    .serialNumber(serialNo != null ? serialNo: nextSerialNumber)
                    .chapter(chapter)
                    .createdAt(LocalDateTime.now())
                    .build();

            lectureRepository.save(lecture);
            log.info("Lecture saved successfully with ID: {}", lecture.getLectureId());

            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Lecture created successfully")
                            .data(
                                    LectureResponse.builder()
                                            .lectureId(lecture.getLectureId())
                                            .lectureNo(lecture.getSerialNumber())
                                            .urls(List.of(lecture.getContentURL()))
                                            .build()
                            )
                            .build(),
                    HttpStatus.CREATED
            );

        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Failed to upload file")
                            .data(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
