package com.jerokysys.commons.domain.enums;

import lombok.Getter;

@Getter
public enum ClassStatus {
    ACTIVE("Activa"),
    INACTIVE("Inactiva"),
    CANCELLED("Cancelada"),
    SUSPENDED("Suspendida");

    private final String description;

    ClassStatus(String description) {
        this.description = description;
    }
}