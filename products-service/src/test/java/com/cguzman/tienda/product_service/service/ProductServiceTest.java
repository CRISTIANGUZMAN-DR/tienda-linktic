package com.cguzman.tienda.product_service.service;

import com.cguzman.tienda.product_service.dto.ProductRequest;
import com.cguzman.tienda.product_service.dto.ProductResponse;
import com.cguzman.tienda.product_service.exception.ProductNotFoundException;
import com.cguzman.tienda.product_service.exception.SkuAlreadyExistsException;
import com.cguzman.tienda.product_service.mapper.ProductMapper;
import com.cguzman.tienda.product_service.model.Product;
import com.cguzman.tienda.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository repository;
    @Mock ProductMapper mapper;
    @InjectMocks ProductService service;

    private ProductRequest request;
    private Product product;
    private ProductResponse response;

    @BeforeEach
    void setUp() {
        request = new ProductRequest();
        request.setSku("PROD-001");
        request.setName("Camiseta Azul");
        request.setPrice(BigDecimal.valueOf(29.99));
        request.setStatus("ACTIVE");

        product = Product.builder()
                .id(UUID.randomUUID())
                .sku("PROD-001")
                .name("Camiseta Azul")
                .price(BigDecimal.valueOf(29.99))
                .status(Product.Status.ACTIVE)
                .build();

        response = ProductResponse.builder()
                .id(product.getId())
                .sku("PROD-001")
                .name("Camiseta Azul")
                .price(BigDecimal.valueOf(29.99))
                .status("ACTIVE")
                .build();
    }

    @Test
    void create_shouldReturnProduct_whenSkuIsUnique() {
        when(repository.existsBySku("PROD-001")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(product);
        when(repository.save(product)).thenReturn(product);
        when(mapper.toResponse(product)).thenReturn(response);

        ProductResponse result = service.create(request);

        assertThat(result.getSku()).isEqualTo("PROD-001");
        verify(repository).save(product);
    }

    @Test
    void create_shouldThrow409_whenSkuAlreadyExists() {
        when(repository.existsBySku("PROD-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(SkuAlreadyExistsException.class)
                .hasMessageContaining("PROD-001");

        verify(repository, never()).save(any());
    }

    @Test
    void findById_shouldThrow404_whenProductNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void findById_shouldReturnProduct_whenExists() {
        when(repository.findById(product.getId())).thenReturn(Optional.of(product));
        when(mapper.toResponse(product)).thenReturn(response);

        ProductResponse result = service.findById(product.getId());

        assertThat(result.getId()).isEqualTo(product.getId());
    }

    @Test
    void delete_shouldThrow404_whenProductNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void update_shouldThrow409_whenNewSkuAlreadyExists() {
        UUID id = product.getId();
        request.setSku("PROD-999");

        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(repository.existsBySku("PROD-999")).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(SkuAlreadyExistsException.class);
    }
}