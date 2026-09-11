package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Students")
@AttributeOverrides({
        @AttributeOverride(name = "phone", column = @Column(name = "personal_phone", length = 20)),
        @AttributeOverride(name = "email", column = @Column(name = "personal_email", length = 100))
})
@Getter
@Setter
public class Student extends Person {

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // Relación: Muchos estudiantes pueden pertenecer a un mismo tutor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id")
    private Guardian guardian;
}