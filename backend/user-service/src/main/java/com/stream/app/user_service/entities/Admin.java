package com.stream.app.user_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    private String AdminId; // Same as User ID

    private String email;
    private String phone;
    private String name;
    private String role; // ADMIN or TEACHER
}
