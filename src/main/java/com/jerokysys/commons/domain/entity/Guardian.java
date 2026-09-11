package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Guardians")
@Getter
@Setter
public class Guardian extends Person {

    @Column(name = "relationship", length = 50)
    private String relationship;

    // Relación bidireccional: Un tutor puede tener muchos estudiantes.
    // "mappedBy" le dice a JPA que la llave foránea real está en la clase Student.
    @OneToMany(mappedBy = "guardian", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students;
}