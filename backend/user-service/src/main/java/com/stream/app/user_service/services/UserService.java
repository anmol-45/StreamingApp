package com.stream.app.user_service.services;


import com.stream.app.user_service.entities.User;

public interface UserService {
    User getDetails(String email);
//    String updateDetails(String email, String name);
//    String deleteUser(String email);
}