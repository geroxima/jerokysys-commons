package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Enrollments")
@Getter
@Setter
public class Enrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private DanceClass danceClass;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate = LocalDate.now();

    // Precio acordado por beca o descuento especial (si es null, aplica el precio de DanceClass)
    @Column(name = "agreed_price", precision = 10, scale = 2)
    private BigDecimal agreedPrice;

    @Column(name = "status", length = 20)
    private String status = "Active";
}