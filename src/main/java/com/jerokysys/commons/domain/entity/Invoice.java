package com.jerokysys.commons.domain.entity;

import com.jerokysys.commons.domain.base.BaseEntity;
import com.jerokysys.commons.domain.enums.InvoiceStatus;
import com.jerokysys.commons.domain.enums.SaleCondition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Invoices")
@Getter
@Setter
public class Invoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "timbrado", length = 20, nullable = false)
    private String timbrado;

    @Column(name = "invoice_number", length = 20, nullable = false)
    private String invoiceNumber;

    @Column(name = "tax_id", length = 20, nullable = false)
    private String taxId;

    @Column(name = "business_name", length = 150, nullable = false)
    private String businessName;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_condition", length = 20)
    private SaleCondition saleCondition = SaleCondition.CONTADO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "total_iva", precision = 10, scale = 2)
    private BigDecimal totalIva = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private InvoiceStatus paymentStatus = InvoiceStatus.PENDING;

    // Relación Cabecera-Detalle: Una factura tiene múltiples líneas de detalle
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceDetail> details = new ArrayList<>();
}