package com.cguzman.tienda.inventory_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class PurchaseRequest {

    @NotNull(message = "productId is required")
    private UUID productId;

    @Min(value = 1, message = "quantity must be >= 1")
    private int quantity;
}