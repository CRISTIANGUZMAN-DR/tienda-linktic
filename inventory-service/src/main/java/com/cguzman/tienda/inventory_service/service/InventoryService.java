package com.cguzman.tienda.inventory_service.service;

import com.cguzman.tienda.inventory_service.client.ProductsClient;
import com.cguzman.tienda.inventory_service.dto.*;
import com.cguzman.tienda.inventory_service.exception.InsufficientStockException;
import com.cguzman.tienda.inventory_service.model.*;
import com.cguzman.tienda.inventory_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PurchaseIdempotencyRepository idempotencyRepository;
    private final ProductsClient productsClient;

    public InventoryResponse getInventory(UUID productId) {
        // Valida que el producto exista en Products Service
        productsClient.productExists(productId);

        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .available(0)
                        .reserved(0)
                        .build());

        return toResponse(inv);
    }

    @Transactional
    public PurchaseResponse purchase(PurchaseRequest req, String idempotencyKey) {

        // 1. Idempotencia: si ya procesamos esta compra, devolvemos el mismo resultado
        if (idempotencyRepository.existsById(idempotencyKey)) {
            log.info("Duplicate purchase request detected: {}", idempotencyKey);
            Inventory inv = inventoryRepository.findByProductId(req.getProductId()).orElseThrow();
            return PurchaseResponse.builder()
                    .status("SUCCESS")
                    .productId(req.getProductId())
                    .quantityBought(req.getQuantity())
                    .remainingStock(inv.getAvailable())
                    .build();
        }

        // 2. Validar que el producto existe
        productsClient.productExists(req.getProductId());

        // 3. Bloqueo pesimista: solo una transacción puede modificar este inventario a la vez
        Inventory inv = inventoryRepository
                .findByProductIdForUpdate(req.getProductId())
                .orElseGet(() -> Inventory.builder()
                        .productId(req.getProductId())
                        .available(0)
                        .reserved(0)
                        .build());

        // 4. Validar stock suficiente
        if (inv.getAvailable() < req.getQuantity()) {
            throw new InsufficientStockException(inv.getAvailable(), req.getQuantity());
        }

        // 5. Descontar stock
        inv.setAvailable(inv.getAvailable() - req.getQuantity());
        inventoryRepository.save(inv);

        // 6. Guardar clave de idempotencia para evitar doble descuento
        idempotencyRepository.save(PurchaseIdempotency.builder()
                .idempotencyKey(idempotencyKey)
                .result("SUCCESS")
                .build());

        // 7. Publicar evento (log estructurado por ahora)
        log.info("InventoryChanged productId={} quantityBought={} remaining={}",
                req.getProductId(), req.getQuantity(), inv.getAvailable());

        return PurchaseResponse.builder()
                .status("SUCCESS")
                .productId(req.getProductId())
                .quantityBought(req.getQuantity())
                .remainingStock(inv.getAvailable())
                .build();
    }

    @Transactional
    public InventoryResponse setInventory(SetInventoryRequest req) {
        productsClient.productExists(req.getProductId());
        Inventory inv = inventoryRepository.findByProductId(req.getProductId())
                .orElseGet(() -> Inventory.builder()
                        .productId(req.getProductId())
                        .reserved(0)
                        .build());
        inv.setAvailable(req.getAvailable());
        return toResponse(inventoryRepository.save(inv));
    }

    private InventoryResponse toResponse(Inventory inv) {
        return InventoryResponse.builder()
                .productId(inv.getProductId())
                .available(inv.getAvailable())
                .reserved(inv.getReserved())
                .build();
    }
}