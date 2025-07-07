package com.stream.app.user_service.controllers;


import com.stream.app.user_service.dto.UserInfo;
import com.stream.app.user_service.repositories.UserRepo;
import com.stream.app.user_service.services.authService.userService.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    @PostMapping("/personal-details")
    public ResponseEntity<?> updateDetails(@RequestBody UserInfo info, HttpServletRequest request){
        return userService.updateDetails(info , request);
    }
}
