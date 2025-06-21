package com.stream.app.user_service.controllers;

import com.stream.app.user_service.dto.AuthResponse;
import com.stream.app.user_service.dto.SignInRequestDto;
import com.stream.app.user_service.dto.SignUpRequestDto;
import com.stream.app.user_service.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody SignUpRequestDto user){

        return authService.registerUser(user);

    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> logInUser(@RequestBody SignInRequestDto user){

        return authService.logInUser(user);

    }
}
