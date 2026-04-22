package com.cguzman.tienda.inventory_service.controller;

import com.cguzman.tienda.inventory_service.dto.*;
import com.cguzman.tienda.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Gestión de inventario y compras")
public class InventoryController {

    private final InventoryService service;

    @GetMapping("/api/inventory/{productId}")
    @Operation(summary = "Consultar inventario", description = "Valida que el producto exista en Products Service")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable UUID productId) {
        return ResponseEntity.ok(service.getInventory(productId));
    }

    @PostMapping("/api/purchases")
    @Operation(summary = "Realizar compra", description = "Descuenta stock. Soporta Idempotency-Key en header")
    public ResponseEntity<PurchaseResponse> purchase(
            @Valid @RequestBody PurchaseRequest req,
            @RequestHeader(value = "Idempotency-Key", required = false,
                    defaultValue = "") String idempotencyKey
    ) {
        if (idempotencyKey.isBlank()) {
            idempotencyKey = UUID.randomUUID().toString();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.purchase(req, idempotencyKey));
    }

    @PostMapping("/api/inventory")
    @Operation(summary = "Setear inventario", description = "Crea o actualiza el stock de un producto")
    public ResponseEntity<InventoryResponse> setInventory(@RequestBody @Valid SetInventoryRequest req) {
        return ResponseEntity.ok(service.setInventory(req));
    }
}