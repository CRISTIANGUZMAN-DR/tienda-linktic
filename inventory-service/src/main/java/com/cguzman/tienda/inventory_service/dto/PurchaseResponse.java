package com.cguzman.tienda.inventory_service.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class PurchaseResponse {
    private String status;       // SUCCESS
    private UUID productId;
    private int quantityBought;
    private int remainingStock;
}