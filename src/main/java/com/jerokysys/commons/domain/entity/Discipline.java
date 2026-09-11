package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Disciplines")
@Getter
@Setter
public class Discipline extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Una disciplina puede tener muchas clases (de danza)
    @OneToMany(mappedBy = "discipline", fetch = FetchType.LAZY)
    private List<DanceClass> classes;
}