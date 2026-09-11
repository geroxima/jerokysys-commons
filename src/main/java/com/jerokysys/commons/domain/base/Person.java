package com.jerokysys.commons.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase base para todas las personas del sistema (Estudiantes, Tutores, Instructores).
 */
@Getter
@Setter
@MappedSuperclass
public abstract class Person extends BaseEntity {

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

}