package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Exams")
@Getter
@Setter
public class Exam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private DanceClass danceClass;

    @Column(name = "exam_name", length = 150, nullable = false)
    private String examName;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    // Un examen recopila las calificaciones de varios alumnos
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamResult> results;
}