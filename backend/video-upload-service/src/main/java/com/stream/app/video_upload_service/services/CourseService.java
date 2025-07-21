package com.stream.app.video_upload_service.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stream.app.video_upload_service.dto.*;
import com.stream.app.video_upload_service.entities.*;
import com.stream.app.video_upload_service.payload.CustomResponseMessage;
import com.stream.app.video_upload_service.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final InstructorRepository instructorRepository;



    @Transactional
    public ResponseEntity<CustomResponseMessage<?>> createCourse(CourseRequest request, MultipartFile image) {
        try {
            // Upload image
            String url = uploadTitleImage(image);

            // Create instructor
            Instructor instructor = createInstructor(request.getInstructor());
            if (instructor == null) {
                log.warn("❌ Instructor creation failed.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(CustomResponseMessage.builder()
                                .message("Instructor creation failed")
                                .data(null)
                                .build());
            }

            // Create course entity
            Courses course = Courses.builder()
                    .courseName(request.getCourseName())
                    .subtitle(request.getSubTitle())
                    .description(request.getDescription())
                    .originalPrice(request.getOriginalPrice())
                    .discountPercent(request.getDiscountPercent())
                    .currency(request.getCurrency())
                    .createdAt(LocalDateTime.now())
                    .tags(request.getTags())
                    .extras(request.getExtras())
                    .imageUrl(url)
                    .bullets(request.getOverviewBullets())
                    .instructor(instructor)
                    .build();

            log.debug("📦 Saving course: {}", course);
            Courses savedCourse = courseRepository.save(course);
            log.info("✅ Course saved with ID: {}", savedCourse.getCourseId());

            return ResponseEntity.ok(
                    CustomResponseMessage.builder()
                            .message("Course created successfully at: " + savedCourse.getCreatedAt())
                            .data(CourseResponse.builder()
                                    .courseId(savedCourse.getCourseId())
                                    .courseName(savedCourse.getCourseName())
                                    .courseSubTitle(savedCourse.getSubtitle())
                                    .description(savedCourse.getDescription())
                                    .price(savedCourse.getDiscountPercent())
                                    .imageUrl(savedCourse.getImageUrl())
                                    .instructorResponse(instructor)
                                    .bullets(savedCourse.getBullets())
                                    .tags(savedCourse.getTags())  // ✅ fixed
                                    .extras(savedCourse.getExtras())
                                    .createdAt(savedCourse.getCreatedAt())
                                    .build())
                            .build()
            );
        } catch (IllegalArgumentException e) {
            log.warn("🚫 Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    CustomResponseMessage.builder()
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to create course: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CustomResponseMessage.builder()
                            .message("Failed to create course")
                            .data(null)
                            .build()
            );
        }
    }


    private Instructor createInstructor(InstructorRequest instructor) {
        Instructor instructorEntity = Instructor.builder()
                .name(instructor.getName())
                .bio(instructor.getBio())
                .experience(instructor.getExperience())
                .title(instructor.getTitle())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            log.debug("📚 Saving Instructor: {}", instructorEntity);
            Instructor saved = instructorRepository.save(instructorEntity);
            log.info("✅ Instructor saved with ID: {}", saved.getInstructorId());
            return saved;
        } catch (Exception e) {
            log.error("❌ Failed to save Instructor: {} - Error: {}", instructorEntity, e.getMessage(), e);
            return null;
        }
    }


    private String uploadTitleImage(MultipartFile image) {
        final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/jpg", "image/webp");

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("🚫 Invalid content type: {}", contentType);
            throw new IllegalArgumentException("Only image files (JPEG, PNG, WEBP) are allowed.");
        }

        try {
            log.debug("📤 Uploading image to Cloudinary...");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap("resource_type", "image", "folder", "courses/")
            );

            String secureUrl = uploadResult.get("secure_url").toString();
            log.info("✅ Image uploaded successfully. URL: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("❌ Failed to upload image to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image to Cloudinary");
        }
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
                .serialNumber(request.getChapterNo() != null ? request.getChapterNo() : nextSerialNumber)
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
    @Transactional
    public ResponseEntity<CustomResponseMessage<?>>  uploadAndSaveLecture(LectureRequest request ,MultipartFile file) {
        log.info("Starting upload for lecture: {}", request.getLectureName());

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", request.getLectureType() == ContentType.VIDEO ? "video" : "auto"
            ));
            String url = (String) uploadResult.get("secure_url");
            log.info("{} uploaded successfully. URL: {}", request.getLectureType(), url);

            Optional<Chapter> optionalChapter = chapterRepository.findById(request.getChapterId());
            if (optionalChapter.isEmpty()) {
                log.warn("❌ Chapter not found with ID: {}", request.getChapterId());
                return new ResponseEntity<>(
                        CustomResponseMessage.builder()
                                .message("Chapter not found with ID: " + request.getChapterId())
                                .data(null)
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            Chapter chapter = optionalChapter.get();

            Integer maxSerialNumber = lectureRepository.findMaxSerialNumberByChapterId(request.getChapterId());
            int nextSerialNumber = (maxSerialNumber != null ? maxSerialNumber : 0) + 1;

            Lecture lecture = Lecture.builder()
                    .lectureName(request.getLectureName())
                    .description(request.getLectureDescription())
                    .contentType(request.getLectureType())
                    .contentURL(url)
                    .serialNumber(request.getSerialNo() != null ? request.getSerialNo(): nextSerialNumber)
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
