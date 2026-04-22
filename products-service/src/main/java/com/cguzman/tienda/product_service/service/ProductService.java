package com.cguzman.tienda.product_service.service;

import com.cguzman.tienda.product_service.dto.*;
import com.cguzman.tienda.product_service.exception.*;
import com.cguzman.tienda.product_service.mapper.ProductMapper;
import com.cguzman.tienda.product_service.model.Product;
import com.cguzman.tienda.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Page<ProductResponse> findAll(String status, String search, Pageable pageable) {
        Product.Status statusEnum = null;
        if (status != null) statusEnum = Product.Status.valueOf(status.toUpperCase());
        return repository.findWithFilters(statusEnum, search, pageable).map(mapper::toResponse);
    }

    public ProductResponse findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));
    }

    public ProductResponse create(ProductRequest req) {
        if (repository.existsBySku(req.getSku())) {
            throw new SkuAlreadyExistsException(req.getSku());
        }
        Product saved = repository.save(mapper.toEntity(req));
        log.info("Product created: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    public ProductResponse update(UUID id, ProductRequest req) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        // Si cambia el SKU, validar que no exista
        if (!product.getSku().equals(req.getSku()) && repository.existsBySku(req.getSku())) {
            throw new SkuAlreadyExistsException(req.getSku());
        }

        product.setSku(req.getSku());
        product.setName(req.getName());
        product.setPrice(req.getPrice());
        product.setStatus(Product.Status.valueOf(req.getStatus()));

        return mapper.toResponse(repository.save(product));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) throw new ProductNotFoundException(id.toString());
        repository.deleteById(id);
        log.info("Product deleted: {}", id);
    }
}