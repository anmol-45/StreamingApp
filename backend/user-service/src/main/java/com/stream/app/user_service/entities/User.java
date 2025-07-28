package com.stream.app.user_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    private String id; // UUID or email/phone as PK

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String name;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isPremium = false;
    private List<Long> courseId;

}
