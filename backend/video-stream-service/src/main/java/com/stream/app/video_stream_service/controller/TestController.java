package com.stream.app.video_stream_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stream")
public class TestController {

    @GetMapping("/test")
    public String getDetails(){
        return "success";
    }
}
