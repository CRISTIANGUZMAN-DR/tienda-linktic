package com.cguzman.tienda.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", message = "Price must be >= 0")
    private BigDecimal price;

    private String status = "ACTIVE";
}