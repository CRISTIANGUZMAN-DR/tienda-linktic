package com.cguzman.tienda.inventory_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// Guarda las compras ya procesadas para evitar doble descuento
@Entity
@Table(name = "purchase_idempotency")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseIdempotency {

    @Id
    private String idempotencyKey;

    private String result; // "SUCCESS" o "FAILED"

    @CreationTimestamp
    private LocalDateTime createdAt;
}