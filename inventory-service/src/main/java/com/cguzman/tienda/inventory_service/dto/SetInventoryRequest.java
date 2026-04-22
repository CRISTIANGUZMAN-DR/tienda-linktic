package com.cguzman.tienda.inventory_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class SetInventoryRequest {
    @NotNull
    private UUID productId;
    @Min(0)
    private int available;
}