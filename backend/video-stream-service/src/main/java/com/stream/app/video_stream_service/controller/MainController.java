package com.stream.app.video_stream_service.controller;


import com.stream.app.video_stream_service.service.MainService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stream")
public class MainController {

    @Autowired
    private MainService mainService;


    @GetMapping("/lecture/{}")
    public ResponseEntity<?> getLecture(@PathVariable Integer lectureId, HttpServletRequest request){

        return mainService.getLecture(lectureId ,request);
    }


}
