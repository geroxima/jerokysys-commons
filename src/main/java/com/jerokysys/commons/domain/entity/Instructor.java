package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Instructors")
@Getter
@Setter
public class Instructor extends Person {

    // Relación bidireccional ManyToMany con las clases que imparte
    @ManyToMany(mappedBy = "instructors", fetch = FetchType.LAZY)
    private Set<DanceClass> classes = new HashSet<>();
}