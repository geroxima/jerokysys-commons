package com.jerokysys.commons.domain.enums;

import lombok.Getter;

@Getter
public enum SaleCondition {
    CONTADO("Venta al Contado"),
    CREDITO("Venta a Crédito");

    private final String description;

    SaleCondition(String description) {
        this.description = description;
    }
}