package com.cguzman.tienda.inventory_service.client;

import com.cguzman.tienda.inventory_service.exception.ProductNotFoundException;
import com.cguzman.tienda.inventory_service.exception.ProductServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
@Slf4j
public class ProductsClient {

    private final WebClient webClient;

    public ProductsClient(@Value("${products.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-KEY", "internal-secret-key")
                .build();
    }

    @CircuitBreaker(name = "productsService", fallbackMethod = "fallbackProductExists")
    @Retry(name = "productsService")
    public boolean productExists(UUID productId) {
        try {
            webClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            throw new ProductNotFoundException(productId.toString());
        }
    }

    // Se ejecuta cuando el circuit breaker está abierto
    public boolean fallbackProductExists(UUID productId, Exception ex) {
        log.error("Circuit breaker open for Products Service: {}", ex.getMessage());
        throw new ProductServiceUnavailableException();
    }
}