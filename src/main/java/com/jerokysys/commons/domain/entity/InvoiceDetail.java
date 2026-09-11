package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Invoice_Details")
@Getter
@Setter
public class InvoiceDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    // IVA aplicable: 10, 5 o 0 (Exento)
    @Column(name = "vat_rate")
    private Integer vatRate = 10;

    @Column(name = "corresponding_month", length = 20)
    private String correspondingMonth;
}