package com.stream.app.video_stream_service.service;

import com.stream.app.video_stream_service.entities.Lecture;
import com.stream.app.video_stream_service.payload.CustomResponseMessage;
import com.stream.app.video_stream_service.repositories.LectureRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class MainService {

    private static final Logger logger = LoggerFactory.getLogger(MainService.class);

    @Autowired
    private LectureRepository lectureRepository;

    public ResponseEntity<CustomResponseMessage<?>> getLecture(Integer chapterId, HttpServletRequest request) {
        logger.info("Fetching lectures for chapterId: {}", chapterId);

        try {
            List<Lecture> lectureList = lectureRepository.findByChapter_ChapterId(chapterId);

            if (lectureList == null || lectureList.isEmpty()) {
                logger.warn("No lectures found for chapterId: {}", chapterId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        CustomResponseMessage.<List<Lecture>>builder()
                                .message("No lectures found for Chapter ID: " + chapterId)
                                .data(Collections.emptyList())
                                .build()
                );
            }

            logger.info("Fetched {} lectures for chapterId: {}", lectureList.size(), chapterId);
            return ResponseEntity.ok(
                    CustomResponseMessage.<List<Lecture>>builder()
                            .message("Lectures fetched successfully")
                            .data(lectureList)
                            .build()
            );

        } catch (Exception e) {
            logger.error("Error while fetching lectures for chapterId: {}", chapterId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CustomResponseMessage.<List<Lecture>>builder()
                            .message("Internal server error while fetching lectures.")
                            .data(Collections.emptyList())
                            .build()
            );
        }
    }

}
