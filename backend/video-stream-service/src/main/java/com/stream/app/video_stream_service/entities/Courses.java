package com.stream.app.video_stream_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "courses")
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    private String courseName;

    private String subtitle;

    @Lob
    private String description;

    private BigDecimal originalPrice;

    private Integer discountPercent;

    private String currency;

    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> extras;

    @ElementCollection
    private List<String> bullets;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects;
}
