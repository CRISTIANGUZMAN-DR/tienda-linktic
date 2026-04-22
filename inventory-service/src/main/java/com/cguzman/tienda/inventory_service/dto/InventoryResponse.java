package com.cguzman.tienda.inventory_service.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class InventoryResponse {
    private UUID productId;
    private int available;
    private int reserved;
}