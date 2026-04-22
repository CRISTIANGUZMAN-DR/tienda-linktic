package com.cguzman.tienda.inventory_service.service;

import com.cguzman.tienda.inventory_service.client.ProductsClient;
import com.cguzman.tienda.inventory_service.dto.PurchaseRequest;
import com.cguzman.tienda.inventory_service.dto.PurchaseResponse;
import com.cguzman.tienda.inventory_service.exception.InsufficientStockException;
import com.cguzman.tienda.inventory_service.model.Inventory;
import com.cguzman.tienda.inventory_service.repository.InventoryRepository;
import com.cguzman.tienda.inventory_service.repository.PurchaseIdempotencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock InventoryRepository inventoryRepository;
    @Mock PurchaseIdempotencyRepository idempotencyRepository;
    @Mock ProductsClient productsClient;
    @InjectMocks InventoryService service;

    private UUID productId;
    private Inventory inventory;
    private PurchaseRequest purchaseRequest;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        inventory = Inventory.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .available(10)
                .reserved(0)
                .build();

        purchaseRequest = new PurchaseRequest();
        purchaseRequest.setProductId(productId);
        purchaseRequest.setQuantity(3);
    }

    @Test
    void purchase_shouldDescountStock_whenStockIsSufficient() {
        when(idempotencyRepository.existsById("key-001")).thenReturn(false);
        when(productsClient.productExists(productId)).thenReturn(true);
        when(inventoryRepository.findByProductIdForUpdate(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);

        PurchaseResponse result = service.purchase(purchaseRequest, "key-001");

        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getRemainingStock()).isEqualTo(7);
        verify(inventoryRepository).save(any());
    }

    @Test
    void purchase_shouldThrow409_whenStockInsufficient() {
        purchaseRequest.setQuantity(99);

        when(idempotencyRepository.existsById("key-002")).thenReturn(false);
        when(productsClient.productExists(productId)).thenReturn(true);
        when(inventoryRepository.findByProductIdForUpdate(productId)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> service.purchase(purchaseRequest, "key-002"))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Available: 10");
    }

    @Test
    void purchase_shouldBeIdempotent_whenKeyAlreadyExists() {
        when(idempotencyRepository.existsById("key-003")).thenReturn(true);
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        PurchaseResponse result = service.purchase(purchaseRequest, "key-003");

        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        // No debe descontar stock ni llamar a productsClient
        verify(productsClient, never()).productExists(any());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void getInventory_shouldReturnZero_whenNoInventoryExists() {
        when(productsClient.productExists(productId)).thenReturn(true);
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

        var result = service.getInventory(productId);

        assertThat(result.getAvailable()).isEqualTo(0);
    }

    @Test
    void purchase_shouldNotSaveStock_whenStockInsufficient() {
        purchaseRequest.setQuantity(50);

        when(idempotencyRepository.existsById("key-004")).thenReturn(false);
        when(productsClient.productExists(productId)).thenReturn(true);
        when(inventoryRepository.findByProductIdForUpdate(productId)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> service.purchase(purchaseRequest, "key-004"))
                .isInstanceOf(InsufficientStockException.class);

        verify(inventoryRepository, never()).save(any());
    }
}
