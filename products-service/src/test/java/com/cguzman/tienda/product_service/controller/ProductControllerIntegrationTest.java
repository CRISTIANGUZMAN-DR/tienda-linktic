package com.cguzman.tienda.product_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void createProduct_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "sku": "TEST-001",
                        "name": "Producto Test",
                        "price": 19.99,
                        "status": "ACTIVE"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("TEST-001"));
    }

    @Test
    void createProduct_shouldReturn409_whenDuplicateSku() throws Exception {
        String body = """
            {
                "sku": "DUPE-001",
                "name": "Producto",
                "price": 10.00,
                "status": "ACTIVE"
            }
        """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void getProduct_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/products/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listProducts_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void createProduct_shouldReturn422_whenInvalidData() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "sku": "",
                        "name": "",
                        "price": -5
                    }
                """))
                .andExpect(status().isUnprocessableEntity());
    }
}