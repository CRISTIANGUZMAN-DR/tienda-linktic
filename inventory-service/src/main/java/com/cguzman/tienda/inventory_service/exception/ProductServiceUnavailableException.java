package com.cguzman.tienda.inventory_service.exception;

public class ProductServiceUnavailableException extends RuntimeException {
    public ProductServiceUnavailableException() {
        super("Products service is unavailable. Please try again later.");
    }
}