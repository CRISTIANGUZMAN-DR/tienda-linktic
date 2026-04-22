package com.cguzman.tienda.inventory_service.controller;

import com.cguzman.tienda.inventory_service.client.ProductsClient;
import com.cguzman.tienda.inventory_service.exception.ProductNotFoundException;
import com.cguzman.tienda.inventory_service.model.Inventory;
import com.cguzman.tienda.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventoryControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired InventoryRepository inventoryRepository;
    @MockBean ProductsClient productsClient;

    private UUID productId;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
        productId = UUID.randomUUID();
        when(productsClient.productExists(any())).thenReturn(true);
    }

    @Test
    void getInventory_shouldReturn200_withZeroStock() throws Exception {
        mockMvc.perform(get("/api/inventory/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(0));
    }

    @Test
    void purchase_shouldReturn201_whenStockSufficient() throws Exception {
        // Crear inventario previo
        inventoryRepository.save(Inventory.builder()
                .productId(productId)
                .available(10)
                .reserved(0)
                .build());

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content("""
                    {
                        "productId": "%s",
                        "quantity": 3
                    }
                """.formatted(productId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.remainingStock").value(7));
    }

    @Test
    void purchase_shouldReturn409_whenStockInsufficient() throws Exception {
        inventoryRepository.save(Inventory.builder()
                .productId(productId)
                .available(2)
                .reserved(0)
                .build());

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content("""
                    {
                        "productId": "%s",
                        "quantity": 99
                    }
                """.formatted(productId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void purchase_shouldReturn404_whenProductNotFound() throws Exception {
        when(productsClient.productExists(any()))
                .thenThrow(new ProductNotFoundException(productId.toString()));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content("""
                    {
                        "productId": "%s",
                        "quantity": 1
                    }
                """.formatted(productId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void purchase_shouldBeIdempotent_whenSameKeyUsedTwice() throws Exception {
        inventoryRepository.save(Inventory.builder()
                .productId(productId)
                .available(10)
                .reserved(0)
                .build());

        String body = """
            {
                "productId": "%s",
                "quantity": 3
            }
        """.formatted(productId);

        String idempotencyKey = UUID.randomUUID().toString();

        // Primera compra
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", idempotencyKey)
                        .content(body))
                .andExpect(status().isCreated());

        // Segunda compra con misma key — no debe descontar stock de nuevo
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", idempotencyKey)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.remainingStock").value(7));
    }
}