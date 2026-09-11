package com.jerokysys.commons.domain.enums;

import lombok.Getter;

@Getter
public enum InvoiceStatus {
    PENDING("Pendiente de Pago"),
    PAID("Pagada"),
    CANCELLED("Anulada");

    private final String description;

    InvoiceStatus(String description) {
        this.description = description;
    }
}