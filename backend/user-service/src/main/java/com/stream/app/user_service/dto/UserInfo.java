package com.stream.app.user_service.dto;

import com.stream.app.user_service.entities.Role;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String id;
    private String name;
    private String email;
    private boolean isPremium;
    private String phoneNumber;
    private Role role;
//    private List<Courses> Courses;
}
