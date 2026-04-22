package com.cguzman.tienda.product_service.exception;

public class SkuAlreadyExistsException extends RuntimeException {
    public SkuAlreadyExistsException(String sku) {
        super("SKU already exists: " + sku);
    }
}