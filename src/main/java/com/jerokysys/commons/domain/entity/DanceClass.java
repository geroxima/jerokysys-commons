package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.BaseEntity;
import com.jerokysys.commons.domain.enums.ClassStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Classes")
@Getter
@Setter
public class DanceClass extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @Column(name = "level", length = 50, nullable = false)
    private String level;

    @Column(name = "day_of_week", length = 100, nullable = false)
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "monthly_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal monthlyPrice;

    // Uso de Enum tipado
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ClassStatus status = ClassStatus.ACTIVE;

    // Relación ManyToMany: Una clase puede tener varios profesores y un profesor varias clases.
    // Mapea la tabla intermedia "Class_Instructors"
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Class_Instructors",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructor> instructors = new HashSet<>();

    // Una clase tiene múltiples inscripciones de alumnos
    @OneToMany(mappedBy = "danceClass", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;
}