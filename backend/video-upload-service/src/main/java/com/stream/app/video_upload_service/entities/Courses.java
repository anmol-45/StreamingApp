package com.stream.app.video_upload_service.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CourseId;

    private String name;


//    @OneToMany(mappedBy = "Courses")
//    private List<Video> videoList = new ArrayList<>();

}