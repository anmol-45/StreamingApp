package com.stream.app.user_service.controllers;

import com.stream.app.user_service.payload.CustomResponseMessage;
import com.stream.app.user_service.services.authService.userService.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    @GetMapping("/personal-details")
    public ResponseEntity<CustomResponseMessage<?>> updateDetails(HttpServletRequest request){
        System.out.println("fetched");
        return userService.getDetails(request);
    }
}
