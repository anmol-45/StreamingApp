package com.stream.app.video_upload_service.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stream.app.video_upload_service.dto.CourseRequest;
import com.stream.app.video_upload_service.entities.Chapter;
import com.stream.app.video_upload_service.entities.ContentType;
import com.stream.app.video_upload_service.entities.Courses;
import com.stream.app.video_upload_service.entities.Lecture;
import com.stream.app.video_upload_service.repositories.LectureRepository;
import com.stream.app.video_upload_service.repositories.ChapterRepository;
import com.stream.app.video_upload_service.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LectureRepository lectureRepository;
    private final Cloudinary cloudinary;

    public ResponseEntity<?> createCourse(CourseRequest request) {
        Courses course = new Courses();
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setPrice(new BigDecimal(String.valueOf(request.getPrice())));
        course.setCreatedAt(LocalDateTime.now());

        try{

            log.debug("Saving course: {}", course);
            courseRepository.save(course);

            return new ResponseEntity<>("course saved successfully at : " + course.getCourseID() + course.getCreatedAt() , HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> uploadLecture(Integer chapterId, MultipartFile file, String title, String Description) {
        logger.info("Starting upload for video title: {}", title);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "video"
            ));

            String url = (String) uploadResult.get("secure_url");
            logger.info("Video uploaded successfully to Cloudinary. URL: {}", url);

            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));


            Lecture content = new Lecture();
            content.setChapter(chapter);
            content.setTitle(title);
            content.setContentType(ContentType.VIDEO);
            content.setContentURL(url);
            content.setSerialNumber(1);
            content.setCreatedAt(LocalDateTime.now());


            lectureRepository.save(content);

            log.info("lecture saved successfully at URL: {}", url);
            return new ResponseEntity<>("lecture saved successfully: \n contentId: " + content.getLectureID() + "\n url: "+ content.getContentURL() , HttpStatus.OK);

        } catch (IOException e) {
            logger.error("Video upload failed due to IOException: {}", e.getMessage());
            return null;
        }
    }
}
